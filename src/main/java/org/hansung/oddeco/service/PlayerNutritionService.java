package org.hansung.oddeco.service;

import io.papermc.paper.event.player.PlayerTradeEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.hansung.oddeco.core.entity.nutrition.NutritionFacts;
import org.hansung.oddeco.core.entity.nutrition.PlayerNutritionFactRewardData;
import org.hansung.oddeco.core.entity.player.PlayerNutritionState;
import org.hansung.oddeco.core.util.logging.FormattedLogger;
import org.hansung.oddeco.repository.NutritionFactRepository;
import org.hansung.oddeco.repository.PlayerNutritionFactRewardDataRepository;
import org.hansung.oddeco.repository.PlayerNutritionRepository;
import org.hansung.oddeco.service.nutrition.PlayerNutritionStateListener;
import org.hansung.oddeco.service.nutrition.PlayerNutritionStateListenerManager;
import org.hansung.oddeco.service.nutrition.PlayerNutritionStateUpdateListener;
import org.hansung.oddeco.service.nutrition.PlayerNutritionUpdateEvent;

import java.sql.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class PlayerNutritionService implements Listener, PlayerNutritionStateListenerManager {
    private static final Duration DEFAULT_EXHAUSTION_DELAY = Duration.ofSeconds(10);
    private static final int DEFAULT_EXHAUSTION_DECREMENT = 1;

    private final Plugin plugin;
    private final NutritionFactRepository nutritionFactRepository;
    private final PlayerNutritionRepository playerNutritionRepository;
    private final PlayerNutritionFactRewardDataRepository playerNutritionFactRewardDataRepository;
    private final Connection connection;
    private final FormattedLogger logger;
    private final NamespacedKey nutritionKey;
    private final ConcurrentMap<UUID, ScheduledExecutorService> executorServiceMap;
    private final ConcurrentMap<UUID, LocalTime> playerNutritionFactRewardTimestamps;
    private final Set<PlayerNutritionStateListener> listeners;

    private Duration exhaustionDelay;
    private int decrement;
    private NutritionFacts decrementNutritionFacts;

    public PlayerNutritionService(
            Plugin plugin,
            NutritionFactRepository nutritionFactRepository,
            PlayerNutritionRepository playerNutritionRepository,
            PlayerNutritionFactRewardDataRepository playerNutritionFactRewardDataRepository,
            Connection connection,
            FormattedLogger logger) {
        this.plugin = plugin;
        this.nutritionFactRepository = nutritionFactRepository;
        this.playerNutritionRepository = playerNutritionRepository;
        this.playerNutritionFactRewardDataRepository = playerNutritionFactRewardDataRepository;
        this.connection = connection;
        this.logger = logger;
        this.nutritionKey = new NamespacedKey(plugin, "nutrition");

        this.executorServiceMap = new ConcurrentSkipListMap<>();
        this.playerNutritionFactRewardTimestamps = new ConcurrentSkipListMap<>();
        this.listeners = new CopyOnWriteArraySet<>();

        this.exhaustionDelay = DEFAULT_EXHAUSTION_DELAY;
        this.decrement = DEFAULT_EXHAUSTION_DECREMENT;
        this.decrementNutritionFacts = calculateDecrementNutritionFacts();

        initializeSqlTable();
        addListener((PlayerNutritionStateUpdateListener) event -> {
            Player player = event.getPlayer();
            PlayerNutritionState nutritionState = this.playerNutritionRepository
                    .get(player)
                    .orElseThrow();
            player.sendMessage(Component.text("now your nutrition status is " + nutritionState));
        });
        addListener((PlayerNutritionStateUpdateListener) event -> {
            Player player = event.getPlayer();
            PlayerNutritionFactRewardData data = this.playerNutritionFactRewardDataRepository
                    .get(player)
                    .orElseThrow();
            int decrement = data.getNutritionDecrement();
            NutritionFacts nutritionFacts = event.getUpdatedNutritionFactAmounts();
            if (nutritionFacts
                    .stream()
                    .allMatch(info -> info.getAmount() > 0)) {
                return;
            }
            if (nutritionFacts
                    .stream()
                    .allMatch(info -> info.getAmount() + decrement == 0)) {
                player.sendMessage(Component.text("Successfully consumed " + decrement + " of each nutrition fact"));
            } else {
                player.sendMessage(Component.text("You have not enough nutrition decrement for this fact"));
            }
        });
    }

    private static int trimRange(int x) {
        return Math.max(0, Math.min(10, x));
    }

    public NamespacedKey getNutritionKey() {
        return this.nutritionKey;
    }

    public Duration getExhaustionDelay() {
        return this.exhaustionDelay;
    }

    public void setExhaustionDelay(Duration delay) {
        this.exhaustionDelay = delay;
    }

    public int getExhaustionDecrement() {
        return this.decrement;
    }

    public void setExhaustionDecrement(int decrement) {
        this.decrement = decrement;
        this.decrementNutritionFacts = calculateDecrementNutritionFacts();
    }

    public void setPlayerExhaustionDelay(Player player, Duration delay) {
        PlayerNutritionFactRewardData data = this.playerNutritionFactRewardDataRepository
                .get(player)
                .orElseThrow();
        Duration previousDelay = data.getDelay();
        data.setDelay(delay);
        this.playerNutritionFactRewardDataRepository.update(data);
        updateNutritionConsumptionScheduler(player, previousDelay, delay);
    }

    public void setPlayerExhaustionDecrement(Player player, int decrement) {
        PlayerNutritionFactRewardData data = this.playerNutritionFactRewardDataRepository
                .get(player)
                .orElseThrow();
        data.setNutritionDecrement(decrement);
        this.playerNutritionFactRewardDataRepository.update(data);
    }

    // +----------------------------------------------------------------+
    // | !! Trade event test code                                       |
    // | Players can trade items only with nitwit villagers.            |
    // +----------------------------------------------------------------+
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTrade(PlayerTradeEvent event) {
        AbstractVillager abstractVillager = event.getVillager();
        if (abstractVillager instanceof Villager villager) {
            Villager.Profession profession = villager.getProfession();
            NamespacedKey key = profession.getKey();
            if (!key.getKey().equals("nitwit")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LocalTime lastRewardTime = LocalTime.now();
        UUID uuid = player.getUniqueId();
        if (!this.playerNutritionRepository.contains(player)) {
            this.playerNutritionRepository.create(player);
        }
        if (!this.playerNutritionFactRewardDataRepository.contains(player)) {
            this.playerNutritionFactRewardDataRepository.create(player);
        }
        this.playerNutritionRepository
                .get(player)
                .ifPresent(nutrition -> {
                    this.logger.info("Player %s successfully retrieved nutrition info", player.getName());
                    this.logger.info("%s", nutrition);
                });
        this.playerNutritionFactRewardDataRepository
                .get(player)
                .ifPresent(data -> {
                    this.logger.info("Player %s successfully retrieved nutrition reward data", player.getName());
                    this.logger.info("%s", data);
                    initializePlayerNutritionFactReward(data);
                    this.playerNutritionFactRewardTimestamps.put(uuid, lastRewardTime);
                    if (this.executorServiceMap.containsKey(uuid)) {
                        ScheduledExecutorService executorService = this.executorServiceMap.get(uuid);
                        executorService.shutdown();
                    }
                    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                    this.executorServiceMap.put(uuid, executorService);
                    long delay = data.getDelay().toMillis();
                    executorService.scheduleAtFixedRate(updatePlayerNutritionFacts(player), delay, delay, TimeUnit.MILLISECONDS);
                });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (this.executorServiceMap.containsKey(uuid)) {
            ScheduledExecutorService executorService = this.executorServiceMap.remove(uuid);
            executorService.shutdown();
        }
        this.playerNutritionFactRewardTimestamps.remove(uuid);
    }

    // TODO: implement player nutrition reward repository && attach to PlayerNutritionService
    // TODO: implement advancement reward repository && create DB

    @EventHandler
    public void onPlayerCraftItem(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        Recipe recipe = event.getRecipe();
        ItemStack resultItem = recipe.getResult();
        PersistentDataContainer container = resultItem
                .getItemMeta()
                .getPersistentDataContainer();
        if (item == null) {
            return;
        }
        this.logger.info("crafted %s", item.getType().name());
        if (container.has(this.nutritionKey)) {
            item.setItemMeta(resultItem.getItemMeta());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        Block targetBlock = event.getBlock();
        PersistentDataContainer container = item
                .getItemMeta()
                .getPersistentDataContainer();
        if (container.has(this.nutritionKey)) {
            targetBlock.setMetadata("edible", new FixedMetadataValue(this.plugin, item.getItemMeta()));
            this.logger.info("Successfully attached item meta to block");
        }
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        Block block = event.getBlock();
        List<Item> items = event.getItems();
        List<MetadataValue> metadata = block.getMetadata("edible");
        if (metadata.isEmpty()) {
            return;
        }
        MetadataValue metadataValue = metadata.getFirst();
        if (!(metadataValue.value() instanceof ItemMeta itemMeta)) {
            return;
        }
        for (Item item : items) {
            ItemStack itemStack = item.getItemStack();
            itemStack.setItemMeta(itemMeta);
            item.setItemStack(itemStack);
            this.logger.info("Successfully attached item meta to the item");
        }
    }

    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        PersistentDataContainer container = item
                .getItemMeta()
                .getPersistentDataContainer();
        if (!container.has(this.nutritionKey)) {
            return;
        }
        Player player = event.getPlayer();
        String key = container.get(this.nutritionKey, PersistentDataType.STRING);
        Optional<NutritionFacts> nutritionFacts = this.nutritionFactRepository.get(key);
        if (nutritionFacts.isEmpty()) {
            throw new IllegalStateException("Cannot find nutrition fact for key " + key);
        }
        updateNutritionFacts(player, nutritionFacts.get());
    }

    @Override
    public void addListener(PlayerNutritionStateListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(PlayerNutritionStateListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void removeAllListeners() {
        this.listeners.clear();
    }

    private void updateNutritionConsumptionScheduler(Player player, Duration previousDelay, Duration newDelay) {
        UUID uuid = player.getUniqueId();
        ScheduledExecutorService executorService = this.executorServiceMap.get(uuid);
        if (previousDelay.equals(newDelay)) {
            return;
        }
        LocalTime currentTime = LocalTime.now();
        LocalTime lastRewardTime = this.playerNutritionFactRewardTimestamps.get(uuid);
        Duration elapsedDuration = Duration.between(lastRewardTime, currentTime);
        executorService.shutdownNow();
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        if (elapsedDuration.compareTo(newDelay) >= 0) {
            updatePlayerNutritionFacts(player).run();
        }
        long initialDelayMillis = elapsedDuration
                .minus(newDelay)
                .abs()
                .toMillis();
        long delayMillis = elapsedDuration.toMillis();
        scheduledExecutorService.scheduleAtFixedRate(updatePlayerNutritionFacts(player), initialDelayMillis, delayMillis, TimeUnit.MILLISECONDS);
        this.executorServiceMap.put(uuid, scheduledExecutorService);
    }

    private Runnable updatePlayerNutritionFacts(Player player) {
        return () -> {
            PlayerNutritionFactRewardData data = this.playerNutritionFactRewardDataRepository
                    .get(player)
                    .orElseThrow();
            int decrement = data.getNutritionDecrement();
            updateNutritionFacts(player, calculateDecrementNutritionFacts(decrement));
        };
    }

    private void initializePlayerNutritionFactReward(PlayerNutritionFactRewardData data) {
        if (data.getNutritionDecrement() == 0) {
            data.setNutritionDecrement(this.decrement);
        }
        if (data.getReward() == 0) {
            data.setReward(1);
        }
        if (data.getDelay().isZero()) {
            data.setDelay(this.exhaustionDelay);
        }
        this.playerNutritionFactRewardDataRepository.update(data);
        this.logger.info("Successfully update Player's nutrition fact data %s", data);
    }

    private void initializeSqlTable() {
        try {
            DatabaseMetaData databaseMetaData = this.connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, null, "player_nutrition", new String[]{"TABLE"});
            try (resultSet) {
                if (!resultSet.next()) {
                    try (Statement statement = this.connection.createStatement()) {
                        statement.execute("""
                                           create table player_nutrition (
                                              uuid         varchar not null constraint player_nutrition_pk primary key,
                                              carbohydrate integer not null,
                                              protein      integer not null,
                                              fat          integer not null,
                                              vitamin      integer not null
                                          )
                                          """);
                        statement.execute("""
                                          create unique index player_nutrition_uuid_uindex
                                             on player_nutrition (uuid)
                                          """);
                        this.logger.info("Successfully create table 'player_nutrition'");
                    }
                } else {
                    this.logger.info("found existing table 'player_nutrition'");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private NutritionFacts calculateDecrementNutritionFacts(int decrement) {
        return NutritionFacts.of(-decrement, -decrement, -decrement, -decrement);
    }

    private NutritionFacts calculateDecrementNutritionFacts() {
        return NutritionFacts.of(-this.decrement, -this.decrement, -this.decrement, -this.decrement);
    }

    private void updateNutritionFacts(Player player, NutritionFacts nutritionFacts) {
        LocalTime lastRewardTime = LocalTime.now();
        PlayerNutritionState playerNutritionState = this.playerNutritionRepository
                .get(player)
                .orElseThrow();
        NutritionFacts previousNutritionFacts = NutritionFacts.of(playerNutritionState.asNutritionFacts());
        for (NutritionFacts.Info info : nutritionFacts) {
            int amount = playerNutritionState.getAmount(info.getNutrition());
            playerNutritionState.setAmount(info.getNutrition(), trimRange(amount + info.getAmount()));
            System.out.println("Find nutrition facts: " + playerNutritionState);
        }
        NutritionFacts nextNutritionFacts = NutritionFacts.of(playerNutritionState.asNutritionFacts());
        NutritionFacts increment = nextNutritionFacts.subtractNutritionFacts(previousNutritionFacts);
        PlayerNutritionUpdateEvent event = PlayerNutritionUpdateEvent.of(player, increment);
        getListeners(PlayerNutritionStateUpdateListener.class)
                .forEach(listener -> listener.onPlayerNutritionStateUpdate(event));
        this.playerNutritionFactRewardTimestamps.put(player.getUniqueId(), lastRewardTime);
    }

    private <L extends PlayerNutritionStateListener> Set<L> getListeners(Class<L> listenerType) {
        return this.listeners.stream()
                .filter(listenerType::isInstance)
                .map(listenerType::cast)
                .collect(Collectors.toSet());
    }
}
