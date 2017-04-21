package org.egordorichev.lasttry.world.spawn;

import com.badlogic.gdx.Gdx;
import org.egordorichev.lasttry.LastTry;
import org.egordorichev.lasttry.entity.enemy.Enemies;
import org.egordorichev.lasttry.entity.enemy.Enemy;
import org.egordorichev.lasttry.item.block.Block;
import org.egordorichev.lasttry.util.AdvancedRectangle;
import org.egordorichev.lasttry.util.Camera;
import org.egordorichev.lasttry.util.GenericContainer;
import org.egordorichev.lasttry.util.Log;
import org.egordorichev.lasttry.world.biome.Biome;
import org.egordorichev.lasttry.world.spawn.components.EnemySpawn;
import org.egordorichev.lasttry.world.spawn.components.SpawnRate;

import java.util.ArrayList;
import java.util.List;

/**
 * Spawn system that will spawn monsters in the gameworld based on certain rules.
 * Created by LogoTie on 17/04/2017.
 */
public class SpawnSystem {
    private  Biome biome;
    private int spawnRate;
	private int maxSpawns;
    private int minXGrid;
	private int minYGRID;
	private int maxXGRID;
	private int maxYGrid;
	private int maxXGridForActiveZone;
    private int diffBetweenSpawnedAndMaxSpawns;
    private List<Enemy> activeEnemyEntities = new ArrayList<>();
    private int spawnWeightOfCurrentlyActiveEnemies;

    public void update() {
        if(LastTry.environment.currentBiome.get() == null){
            return;
        }

        this.biome = LastTry.environment.currentBiome.get(); // Get user biome
	    spawnTriggered();
    }

    private boolean ableToSpawnNewEnemy(int maxSpawnsOfBiome, ArrayList<Enemy> enemiesInActiveArea) {

        // TODO Expensive calculation
        // TODO Reached 49 and got stuck as there is no monster with spawn weight of 1, wasting calculations.
        this.spawnWeightOfCurrentlyActiveEnemies = EnemySpawn.calcSpawnWeightOfActiveEnemies(enemiesInActiveArea);

        if(spawnWeightOfCurrentlyActiveEnemies>=maxSpawns){
            return false;
        }

        return true;
    }


    // TODO Split method
    private  void spawnTriggered() {
        // Retrieve max spawns of biome
        final int maxSpawns = this.biome.getSpawnMax();
        final int origSpawnRate = this.biome.getSpawnRate();

        ArrayList<Enemy> enemiesInActiveArea = this.generateEnemiesInActiveArea();

        boolean spaceForNewEnemy = ableToSpawnNewEnemy(maxSpawns, enemiesInActiveArea);

        if(!spaceForNewEnemy){
            return;
        }

        final float spawnRateFinal = this.calculateFinalSpawnRate(origSpawnRate);

        if (!EnemySpawn.shouldEnemySpawn(spawnRateFinal)) {
            return;
        }

        ArrayList<Enemy> eligibleEnemiesForSpawn = EnemySpawn.retrieveEligibleSpawnEnemies(diffBetweenSpawnedAndMaxSpawns);

        if (eligibleEnemiesForSpawn.size() == 0) {
            return;
        }

        Enemy enemyToBeSpawned = EnemySpawn.retrieveRandomEnemy(eligibleEnemiesForSpawn);
        this.spawnEnemy(enemyToBeSpawned);
    }



    private float calculateFinalSpawnRate(int origSpawnRate){

        // Spawn rate is modified based on different factors such as time, events occurring.
        int spawnRate = SpawnRate.calculateSpawnRate(origSpawnRate);

        // Spawn rate refers to 1 in 'Spawn Rate' chance of a monster spawning.
        float percentChanceSpawnRate = 1/(float)spawnRate;

        //TODO Split percentage calc into another method
        float percentageOfSpawnRateAndActiveMonsters;

        int diffBetweenSpawnedAndMaxSpawns = maxSpawns - spawnWeightOfCurrentlyActiveEnemies;

        if (spawnWeightOfCurrentlyActiveEnemies == 0) {
            percentageOfSpawnRateAndActiveMonsters = 1;
        } else {
            percentageOfSpawnRateAndActiveMonsters = ((float) spawnWeightOfCurrentlyActiveEnemies / (float) maxSpawns) * 100;
        }

        float spawnRateFloat = SpawnRate.applyMultiplierToSpawnRate(percentChanceSpawnRate, percentageOfSpawnRateAndActiveMonsters);

        return spawnRateFloat;
    }


    private void spawnEnemy(Enemy enemy) {
        GenericContainer.Pair<Integer> suitableXySpawnPoint = generateEligibleSpawnPoint();
        LastTry.entityManager.spawnEnemy((short)enemy.getID(), suitableXySpawnPoint.getFirst(), suitableXySpawnPoint.getSecond());
    }

    private GenericContainer.Pair<Integer> generateEligibleSpawnPoint() {
        Log.debug("Generating eligible spawn point");
        // Generate inside the active zone
        int xGridSpawnPoint = maxXGridForActiveZone-30; int yGridSpawnPoint = minYGRID;
        int xPixelSpawnPoint = xGridSpawnPoint* Block.SIZE; int yPixelSpawnPoint = yGridSpawnPoint * Block.SIZE;

        GenericContainer.Pair<Integer> xyPoint = new GenericContainer.Pair<>();
        xyPoint.set(xPixelSpawnPoint, yPixelSpawnPoint);

        return xyPoint;
    }

    private void generateMinMaxGridBlockValues() {
        int windowWidth = Gdx.graphics.getWidth();
        int windowHeight = Gdx.graphics.getHeight();
        int tww = windowWidth / Block.SIZE;
        int twh = windowHeight / Block.SIZE;

        // We want to get the further most position of x on the screen, camera is always in the middle so we
        // divide total window width by 2 and divide by blcok size to get grid position
        int tcx = (int) (Camera.game.position.x - windowWidth/2) / Block.SIZE;

        // TODO Change on inversion of y axis
        // We are subtracting because of the inverted y axis otherwise it would be LastTry.camera.position.y+windowheight/2
        int tcy = (int) (LastTry.world.getHeight() - (Camera.game.position.y + windowHeight/2)/Block.SIZE);

        // Checking to make sure y value is not less than 0 - World generated will always start from 0,0 top left.
        this.minYGRID = Math.max(0, tcy - 2);
        this.maxYGrid = Math.min(LastTry.world.getHeight() - 1, tcy + twh + 3);

        // Checking to make y values is not less than 0
        this.minXGrid = Math.max(0, tcx - 2);
        this.maxXGRID = Math.min(LastTry.world.getWidth() - 1, tcx + tww + 2);

        // Active zone is 6 greater
        // TODO Must check that it is not out of bounds.
        this.maxXGridForActiveZone = this.maxXGRID + 25;
    }

    private ArrayList<Enemy> generateEnemiesInActiveArea() {
        // Must clear the list each time, as it has no way of knowing if an entity has died so we must rebuild
        // each time to ensure we have an up to date list
        ArrayList<Enemy> enemiesInActiveArea = new ArrayList<>();
        this.generateMinMaxGridBlockValues();
        List<Enemy> enemyEntities = LastTry.entityManager.retrieveEnemyEntities();

        enemyEntities.stream().forEach(enemy -> {

            // TODO Rethink
            // Checks if the enemy is in the active area and if the enemy is not already in the list, it adds to the list
            if(this.isEnemyInActiveArea(enemy)){
                enemiesInActiveArea.add(enemy);
                // LastTry.debug("Enemy in active area of: "+enemy.getName());
            }
        });

        return enemiesInActiveArea;
    }

    private boolean isEnemyInActiveArea(Enemy enemy) {
        // Get block co ordinates of enemy
        int enemyBlockGridX = enemy.physics.getGridX();
        int enemyBlockGridY = enemy.physics.getGridY();

        // TODO Change on inversion of y axis
        if(enemyBlockGridX>=this.minXGrid&&enemyBlockGridX<=this.maxXGridForActiveZone){
            if(enemyBlockGridY<=this.maxYGrid&&enemyBlockGridY>=this.minYGRID){
                return true;
            }
        }

        return false;
    }

    public void calcArea(){
        float xOfPLayer = LastTry.player.physics.getCenterX();
        float yOfPlayer = LastTry.player.physics.getCenterY();

        AdvancedRectangle advancedRectangle = new AdvancedRectangle(xOfPLayer, yOfPlayer, 6);

        if(advancedRectangle.allSidesInBoundary()){
            Log.debug("All sides in boundary");
            // advancedRectangle.debugSetItemsOnPoints();
        }
    }

    public void calculateNonSpawnSafePlayerArea(){
        // TODO Implement
    }
}