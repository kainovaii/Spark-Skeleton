package fr.kainovaii.spark;

import fr.kainovaii.core.database.DB;
import fr.kainovaii.core.database.Migration;
import fr.kainovaii.core.database.MigrationManager;
import org.javalite.activejdbc.Base;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MigrationTest
{
    private static final Logger logger = Logger.getLogger("MigrationTest");

    @BeforeAll
    public static void setup()
    {
        File dbFile = new File("Spark/test_migration.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }

        DB.initSQLite("Spark/test_migration.db", logger);
    }

    @Test
    public void testMigrationUpAndDown()
    {
        MigrationManager migrations = new MigrationManager(DB.getInstance(), logger);

        migrations.add(new Migration() {
            @Override
            public void up() {
                createTable("test_users", table -> {
                    table.id();
                    table.string("username").notNull();
                    table.string("email").notNull();
                    table.timestamps();
                });
            }

            @Override
            public void down() {
                dropTable("test_users");
            }
        });

        migrations.migrate();

        Boolean tableExists = DB.withConnection(() -> {
            Object result = Base.firstCell(
                    "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='test_users'"
            );
            long count = result instanceof Long ? (Long) result : Long.parseLong(result.toString());
            return count > 0;
        });

        assertTrue(tableExists, "Table test_users should exist after migration");

        migrations.rollback();

        Boolean tableDropped = DB.withConnection(() -> {
            Object result = Base.firstCell(
                    "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='test_users'"
            );
            long count = result instanceof Long ? (Long) result : Long.parseLong(result.toString());
            return count == 0;
        });

        assertTrue(tableDropped, "Table test_users should be dropped after rollback");
    }

    @Test
    public void testMultipleMigrations()
    {
        MigrationManager migrations = new MigrationManager(DB.getInstance(), logger);

        migrations.add(new Migration() {
            @Override
            public void up() {
                createTable("test_products", table -> {
                    table.id();
                    table.string("name").notNull();
                    table.decimal("price", 10, 2).notNull();
                });
            }

            @Override
            public void down() {
                dropTable("test_products");
            }
        });

        migrations.add(new Migration() {
            @Override
            public void up() {
                createTable("test_orders", table -> {
                    table.id();
                    table.integer("product_id").notNull();
                    table.integer("quantity").notNull();
                });
            }

            @Override
            public void down() {
                dropTable("test_orders");
            }
        });

        migrations.migrate();

        Boolean bothTablesExist = DB.withConnection(() -> {
            Object result1 = Base.firstCell(
                    "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='test_products'"
            );
            Object result2 = Base.firstCell(
                    "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='test_orders'"
            );

            long count1 = result1 instanceof Long ? (Long) result1 : Long.parseLong(result1.toString());
            long count2 = result2 instanceof Long ? (Long) result2 : Long.parseLong(result2.toString());

            return count1 > 0 && count2 > 0;
        });

        assertTrue(bothTablesExist, "Both tables should exist after migrations");

        migrations.rollback();
    }
}