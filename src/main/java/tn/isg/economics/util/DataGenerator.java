package tn.isg.economics.util;

import tn.isg.economics.model.ExportData;
import tn.isg.economics.model.MarketIndicator;
import tn.isg.economics.model.ProductType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {
    private static final Random random = new Random();
    private static final String[] DESTINATION_COUNTRIES = {
            "France",      // Tunisia's #1 export partner
            "Italy",       // Major olive oil importer
            "Germany",     // Dates and citrus importer
            "Spain",       // Regional trade partner
            "Libya",       // North African partner
            "Belgium",     // EU distribution hub
            "Netherlands", // EU distribution hub
            "United Kingdom",
            "Russia",      // Citrus fruits
            "USA"          // Dates
    };

    public static List<ExportData> generateExports(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be at least 1");
        }
        System.out.println("Generating " + count + " synthetic export records...");
        List<ExportData> exports = new ArrayList<>();
        // Generate data over the last 2 years
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusYears(2);
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        for (int i = 0; i < count; i++) {
            /// Generate random date within last 2 years
            long randomDays = (long) (random.nextDouble() * daysBetween);
            LocalDate randomDate = startDate.plusDays(randomDays);
            // Generate single export record
            ExportData export = generateSingleExport(randomDate);
            exports.add(export);
        }
        System.out.println("✓ Generated " + exports.size() + " export records successfully!");
        return exports;
    }

    private static ExportData generateSingleExport(LocalDate date) {
        // Pick random product type
        ProductType[] products = ProductType.values();
        ProductType product = products[random.nextInt(products.length)];
        // Get realistic price for this product (with some variation)
        double basePrice = getRealisticPrice(product);
        double priceVariation = (random.nextDouble() - 0.5) * 0.3; // ±15% variation
        double price = basePrice * (1 + priceVariation);
        price = Math.round(price * 100.0) / 100.0; // Round to 2 decimals
        // Generate realistic volume (tons)
        double volume = generateRealisticVolume(product);
        // Pick random destination country
        String country = getRandomCountry();
        // Determine market indicator based on price and season
        MarketIndicator indicator = getMarketIndicator(product, price, date);
        return new ExportData(date, product, price, volume, country, indicator);
    }

    private static double getRealisticPrice(ProductType product) {
        return switch (product) {
            case OLIVE_OIL ->
                // Olive oil: Premium product, 3000-4500 TND/ton
                    3000 + random.nextDouble() * 1500;
            case DATES ->
                // Dates: High-value export, 2000-3500 TND/ton
                    2000 + random.nextDouble() * 1500;
            case CITRUS_FRUITS ->
                // Citrus: Medium value, 1500-2500 TND/ton
                    1500 + random.nextDouble() * 1000;
            case WHEAT ->
                // Wheat: Commodity price, 800-1200 TND/ton
                    800 + random.nextDouble() * 400;
            case TOMATOES ->
                // Tomatoes: Fresh produce, 800-1500 TND/ton
                    800 + random.nextDouble() * 700;
            case PEPPERS ->
                // Peppers: Fresh produce, 1000-1800 TND/ton
                    1000 + random.nextDouble() * 800;
        };
    }

    private static double generateRealisticVolume(ProductType product) {
        double volume = switch (product) {
            case OLIVE_OIL ->
                // Olive oil: Medium shipments, 50-300 tons
                    50 + random.nextDouble() * 250;
            case DATES ->
                // Dates: Medium shipments, 30-150 tons
                    30 + random.nextDouble() * 120;
            case CITRUS_FRUITS ->
                // Citrus: Large shipments, 100-500 tons
                    100 + random.nextDouble() * 400;
            case WHEAT ->
                // Wheat: Very large shipments, 500-2000 tons
                    500 + random.nextDouble() * 1500;
            case TOMATOES ->
                // Tomatoes: Large shipments, 150-600 tons
                    150 + random.nextDouble() * 450;
            case PEPPERS ->
                // Peppers: Medium shipments, 50-250 tons
                    50 + random.nextDouble() * 200;
        };
        return Math.round(volume * 10.0) / 10.0; // Round to 1 decimal
    }

    private static String getRandomCountry() {
        return DESTINATION_COUNTRIES[random.nextInt(DESTINATION_COUNTRIES.length)];
    }

    private static MarketIndicator getMarketIndicator(ProductType product, double price, LocalDate date) {
        // Get average price for this product
        double avgPrice = getAveragePrice(product);
        // Calculate price deviation from average
        double deviation = (price - avgPrice) / avgPrice;
        // Determine indicator based on price deviation
        if (deviation > 0.10) {
            // Price is 10%+ above average → RISING
            return MarketIndicator.RISING;
        } else if (deviation < -0.10) {
            // Price is 10%+ below average → FALLING
            return MarketIndicator.FALLING;
        } else if (Math.abs(deviation) < 0.05) {
            // Price is within 5% of average → STABLE
            return MarketIndicator.STABLE;
        } else {
            // Some products are naturally more volatile (citrus, tomatoes)
            if (product == ProductType.CITRUS_FRUITS || product == ProductType.TOMATOES) {
                return random.nextBoolean() ? MarketIndicator.VOLATILE : MarketIndicator.STABLE;
            } else {
                return MarketIndicator.STABLE;
            }
        }
    }

    private static double getAveragePrice(ProductType product) {
        return switch (product) {
            case OLIVE_OIL -> 3750.0;
            case DATES -> 2750.0;
            case CITRUS_FRUITS -> 2000.0;
            case WHEAT -> 1000.0;
            case TOMATOES -> 1150.0;
            case PEPPERS -> 1400.0;
        };
    }

    public static List<ExportData> generateExportsByDateRange(
            LocalDate startDate,
            LocalDate endDate,
            int recordsPerDay) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        List<ExportData> exports = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            // Generate random number of records for this day (±50% variation)
            int recordsToday = (int) (recordsPerDay * (0.5 + random.nextDouble()));
            for (int i = 0; i < recordsToday; i++) {
                exports.add(generateSingleExport(currentDate));
            }
            currentDate = currentDate.plusDays(1);
        }
        System.out.println("✓ Generated " + exports.size() + " exports from " +
                startDate + " to " + endDate);
        return exports;
    }

    public static List<ExportData> generateExportsByProduct(int count, ProductType productType) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be at least 1");
        }
        List<ExportData> exports = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusYears(2);
        long daysBetween = startDate.until(endDate).getDays();
        for (int i = 0; i < count; i++) {
            long randomDays = random.nextLong(daysBetween);
            LocalDate randomDate = startDate.plusDays(randomDays);
            // Use specified product or random if null
            ProductType product = (productType != null) ?
                    productType :
                    ProductType.values()[random.nextInt(ProductType.values().length)];
            double price = getRealisticPrice(product);
            double priceVariation = (random.nextDouble() - 0.5) * 0.3;
            price = Math.round(price * (1 + priceVariation) * 100.0) / 100.0;
            double volume = generateRealisticVolume(product);
            String country = getRandomCountry();
            MarketIndicator indicator = getMarketIndicator(product, price, randomDate);
            exports.add(new ExportData(randomDate, product, price, volume, country, indicator));
        }
        System.out.println("✓ Generated " + count + " exports" +
                (productType != null ? " for " + productType.getFrenchName() : ""));
        return exports;
    }
}