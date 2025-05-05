package com.gb.p360.repository;

import com.gb.p360.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Configuration
@Profile("local")
public class DatabaseInitializer {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initDatabase(
            UserRepository userRepository,
            FactoryRepository factoryRepository,
            MaterialRepository materialRepository,
            VendorRepository vendorRepository,
            ProcurementRequestRepository requestRepository,
            LineItemRepository lineItemRepository,
            PriceHistoryRepository priceHistoryRepository) {

        return args -> {
            // Check if data already exists (to avoid duplicating on restart)
            System.out.println("Checking if database already contains data...");
            long userCount = userRepository.count();
            long factoryCount = factoryRepository.count();
            long materialCount = materialRepository.count();
            long vendorCount = vendorRepository.count();
            long requestCount = requestRepository.count();

            System.out.println("Current record counts:");
            System.out.println("Users: " + userCount);
            System.out.println("Factories: " + factoryCount);
            System.out.println("Materials: " + materialCount);
            System.out.println("Vendors: " + vendorCount);
            System.out.println("Procurement Requests: " + requestCount);

            if (userRepository.count() > 0) {
                System.out.println("Database already contains data, skipping initialization");
                return;
            }

            System.out.println("Initializing database with test data...");

            try {
                // Create Factories
                System.out.println("Creating factories...");
                List<Factory> factories = createFactories(factoryRepository);
                System.out.println("Created " + factories.size() + " factories");

                // Create Users with roles and factory combinations
                System.out.println("Creating users...");
                Map<Role, List<User>> usersByRole = createUsers(userRepository, factories);
                int totalUsers = usersByRole.values().stream().mapToInt(List::size).sum();
                System.out.println("Created " + totalUsers + " users");

                // Create Materials - Expanded to 20 materials
                System.out.println("Creating materials...");
                List<Material> materials = createMaterials(materialRepository);
                System.out.println("Created " + materials.size() + " materials");

                // Create Vendors - Expanded to 10 vendors
                System.out.println("Creating vendors...");
                List<Vendor> vendors = createVendors(vendorRepository);
                System.out.println("Created " + vendors.size() + " vendors");

                // Create extensive price history
                System.out.println("Creating price history...");
                int priceHistoryCount = createPriceHistory(priceHistoryRepository, materials, vendors);
                System.out.println("Created " + priceHistoryCount + " price history records");

                // Create at least 100 procurement requests with line items
                System.out.println("Creating procurement requests and line items...");
                int[] requestStats = createProcurementRequests(requestRepository, lineItemRepository, factories,
                        usersByRole, materials, vendors, 100);
                System.out.println("Created " + requestStats[0] + " procurement requests with " + requestStats[1] + " line items");

                System.out.println("Database initialization completed successfully!");
            } catch (Exception e) {
                System.err.println("Error during database initialization: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    private List<Factory> createFactories(FactoryRepository factoryRepository) {
        List<Factory> factories = new ArrayList<>();

        String[][] factoryData = {
                {"SunTech", "ST"},
                {"Thermo Care", "TC"},
                {"Geo Pol", "GP"},
                {"Naad Ind", "NI"},
                {"Naad Non", "NN"}
        };

        for (String[] data : factoryData) {
            try {
                Factory factory = new Factory();
                factory.setName(data[0]);
                factory.setCode(data[1]);
                factory.setCreatedAt(LocalDateTime.now());
                factory.setUpdatedAt(LocalDateTime.now());
                factories.add(factoryRepository.save(factory));
                System.out.println("Created factory: " + data[0] + " with code: " + data[1]);
            } catch (Exception e) {
                System.err.println("Error creating factory " + data[0] + ": " + e.getMessage());
            }
        }

        return factories;
    }

    private Map<Role, List<User>> createUsers(UserRepository userRepository, List<Factory> factories) {
        Map<Role, List<User>> usersByRole = new HashMap<>();
        String encodedPassword = passwordEncoder.encode("Test@123");

        // Pre-initialize lists for each role
        for (Role role : Role.values()) {
            usersByRole.put(role, new ArrayList<>());
        }

        // Create 3 users for each role
        for (Role role : Role.values()) {
            System.out.println("Creating users for role: " + role);
            for (int i = 1; i <= 3; i++) {
                try {
                    User user = new User();
                    user.setUsername(role.name().toLowerCase() + "_user" + i);
                    user.setPassword(encodedPassword);
                    user.setRole(role);
                    user.setCreatedAt(LocalDateTime.now());
                    user.setUpdatedAt(LocalDateTime.now());

                    // Assign factories based on patterns
                    Set<Factory> userFactories = new HashSet<>();
                    switch (i) {
                        case 1:
                            // First user gets all factories
                            userFactories.addAll(factories);
                            break;
                        case 2:
                            // Second user gets first 3 factories
                            userFactories.addAll(factories.subList(0, 3));
                            break;
                        case 3:
                            // Third user gets last 2 factories
                            userFactories.addAll(factories.subList(3, 5));
                            break;
                    }
                    user.setFactories(userFactories);

                    User savedUser = userRepository.save(user);
                    usersByRole.get(role).add(savedUser);

                    System.out.println("Created user: " + savedUser.getUsername() + " with role: " + savedUser.getRole() +
                            " and " + userFactories.size() + " factories");
                } catch (Exception e) {
                    System.err.println("Error creating user for role " + role + ": " + e.getMessage());
                }
            }
        }

        return usersByRole;
    }

    private List<Material> createMaterials(MaterialRepository materialRepository) {
        List<Material> materials = new ArrayList<>();

        // Expanded list of 20 materials
        String[][] materialData = {
                {"Steel Sheet", "Sheet"},
                {"Copper Wire", "Meter"},
                {"Aluminum Rod", "Piece"},
                {"Glass Panel", "Panel"},
                {"Silicon Wafer", "Wafer"},
                {"Plastic Granules", "Kg"},
                {"Paint", "Liter"},
                {"Circuit Board", "Piece"},
                {"Rubber Gasket", "Piece"},
                {"Thermal Paste", "Tube"},
                {"Carbon Fiber", "Sheet"},
                {"Titanium Plate", "Plate"},
                {"Solar Cell", "Cell"},
                {"LED Strip", "Meter"},
                {"Ceramic Tile", "Piece"},
                {"Epoxy Resin", "Kg"},
                {"Battery Cell", "Cell"},
                {"Semiconductor Chip", "Piece"},
                {"Optical Lens", "Piece"},
                {"Cooling Fan", "Unit"}
        };

        for (String[] data : materialData) {
            try {
                Material material = new Material();
                material.setName(data[0]);
                material.setUnitOfMeasure(data[1]);
                material.setCreatedAt(LocalDateTime.now());
                material.setUpdatedAt(LocalDateTime.now());
                materials.add(materialRepository.save(material));
                System.out.println("Created material: " + data[0]);
            } catch (Exception e) {
                System.err.println("Error creating material " + data[0] + ": " + e.getMessage());
            }
        }

        return materials;
    }

    private List<Vendor> createVendors(VendorRepository vendorRepository) {
        List<Vendor> vendors = new ArrayList<>();

        // Expanded list of 10 vendors
        String[] vendorNames = {
                "MetalWorks Inc.",
                "ElectroSupply Co.",
                "GlassWorks Ltd.",
                "ChemicalSolutions",
                "TechParts Global",
                "Advanced Materials Corp",
                "Premier Electronics",
                "IndustrialSupply Hub",
                "Silicon Valley Components",
                "Precision Engineering Ltd."
        };

        for (String name : vendorNames) {
            try {
                Vendor vendor = new Vendor();
                vendor.setName(name);
                vendor.setCreatedAt(LocalDateTime.now());
                vendor.setUpdatedAt(LocalDateTime.now());
                vendors.add(vendorRepository.save(vendor));
                System.out.println("Created vendor: " + name);
            } catch (Exception e) {
                System.err.println("Error creating vendor " + name + ": " + e.getMessage());
            }
        }

        return vendors;
    }

    private int createPriceHistory(PriceHistoryRepository priceHistoryRepository,
                                   List<Material> materials,
                                   List<Vendor> vendors) {
        Random random = new Random();
        int count = 0;

        // Create 5 price history entries per material-vendor combination (instead of 3)
        for (Material material : materials) {
            for (Vendor vendor : vendors) {
                // Generate historical prices
                for (int i = 1; i <= 5; i++) {
                    try {
                        PriceHistory priceHistory = new PriceHistory();
                        priceHistory.setMaterial(material);
                        priceHistory.setVendor(vendor);

                        // Random price between 10 and 2000
                        BigDecimal price = BigDecimal.valueOf(10 + random.nextInt(1991));
                        priceHistory.setUnitPrice(price);

                        // Random date in last 90 days (instead of 60)
                        LocalDate orderDate = LocalDate.now().minusDays(random.nextInt(90));
                        priceHistory.setOrderDate(orderDate);

                        priceHistory.setCreatedAt(LocalDateTime.now());
                        priceHistoryRepository.save(priceHistory);
                        count++;
                    } catch (Exception e) {
                        System.err.println("Error creating price history for material " + material.getName() +
                                " and vendor " + vendor.getName() + ": " + e.getMessage());
                    }
                }
            }
        }

        return count;
    }

    private int[] createProcurementRequests(ProcurementRequestRepository requestRepository,
                                            LineItemRepository lineItemRepository,
                                            List<Factory> factories,
                                            Map<Role, List<User>> usersByRole,
                                            List<Material> materials,
                                            List<Vendor> vendors,
                                            int minRequests) {
        Random random = new Random();
        List<User> factoryUsers = usersByRole.get(Role.FACTORY_USER);
        List<User> purchaseTeam = usersByRole.get(Role.PURCHASE_TEAM);
        int requestCount = 0;
        int lineItemCount = 0;

        // Calculate requests per factory to ensure at least minRequests total
        int requestsPerFactory = (int) Math.ceil((double) minRequests / factories.size());
        System.out.println("Will create " + requestsPerFactory + " requests per factory");

        // Create requestsPerFactory requests per factory
        for (Factory factory : factories) {
            System.out.println("Creating procurement requests for factory: " + factory.getName());

            for (int i = 1; i <= requestsPerFactory; i++) {
                try {
                    // Get a random factory user who has access to this factory
                    User creator = getRandomUserWithFactory(factoryUsers, factory);
                    if (creator == null) {
                        System.err.println("No eligible factory user found for factory " + factory.getName());
                        continue;
                    }

                    ProcurementRequest request = new ProcurementRequest();
                    request.setFactory(factory);
                    request.setUniqueIdentifier(factory.getCode() + "-" + String.format("%06d", i));

                    // Varied priorities - with more URGENT ones
                    Priority[] priorities = {
                            Priority.NORMAL, Priority.NORMAL, Priority.URGENT,
                            Priority.NORMAL, Priority.URGENT
                    };
                    request.setPriority(priorities[random.nextInt(priorities.length)]);

                    // Varied request dates over the last 120 days (instead of 30)
                    request.setRequestDate(LocalDateTime.now().minusDays(random.nextInt(120)));

                    request.setRemarks("Test request " + i + " for " + factory.getName() +
                            ". Priority: " + request.getPriority() +
                            ". Created on " + request.getRequestDate());
                    request.setCreatedBy(creator);
                    request.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(120)));
                    request.setUpdatedAt(LocalDateTime.now());

                    // Distribute statuses more evenly
                    // 25% DRAFTED, 15% DISCARDED, 60% SUBMITTED
                    int statusRandom = random.nextInt(100);
                    RequestStatus requestStatus;
                    if (statusRandom < 25) {
                        requestStatus = RequestStatus.DRAFTED;
                    } else if (statusRandom < 40) {
                        requestStatus = RequestStatus.DISCARDED;
                    } else {
                        requestStatus = RequestStatus.SUBMITTED;
                    }
                    request.setStatus(requestStatus);

                    // For SUBMITTED status, add submission details
                    if (requestStatus == RequestStatus.SUBMITTED) {
                        request.setSubmittedAt(LocalDateTime.now().minusDays(random.nextInt(100)));

                        // Assign random owner from purchase team who has access to this factory
                        User owner = getRandomUserWithFactory(purchaseTeam, factory);
                        if (owner != null) {
                            request.setOwner(owner);
                        } else {
                            System.err.println("No eligible purchase team user found for factory " + factory.getName());
                        }
                    }

                    ProcurementRequest savedRequest = requestRepository.save(request);
                    requestCount++;
                    System.out.println("Created request: " + savedRequest.getUniqueIdentifier() +
                            " with status: " + savedRequest.getStatus());

                    // Add more line items - between 1 and 10 per request (instead of 1-5)
                    int itemsForRequest = 1 + random.nextInt(10);
                    System.out.println("Adding " + itemsForRequest + " line items to request " +
                            savedRequest.getUniqueIdentifier());

                    for (int j = 0; j < itemsForRequest; j++) {
                        try {
                            // Select random material
                            Material material = materials.get(random.nextInt(materials.size()));

                            LineItem lineItem = new LineItem();
                            lineItem.setProcurementRequest(savedRequest);
                            lineItem.setMaterial(material);
                            lineItem.setUnit(material.getUnitOfMeasure());

                            // Higher quantity range - between 1 and 500
                            lineItem.setRequestedQuantity(BigDecimal.valueOf(1 + random.nextInt(500)));
                            lineItem.setCreatedAt(LocalDateTime.now());
                            lineItem.setUpdatedAt(LocalDateTime.now());

                            // For submitted requests, set line item status and details
                            if (savedRequest.getStatus() == RequestStatus.SUBMITTED) {
                                // Distribution of line item statuses for SUBMITTED requests
                                int statusChance = random.nextInt(100);
                                LineItemStatus lineItemStatus;

                                if (statusChance < 20) {
                                    lineItemStatus = LineItemStatus.VENDOR_DISCUSSION;
                                } else if (statusChance < 40) {
                                    lineItemStatus = LineItemStatus.ORDER_PLACED;
                                } else if (statusChance < 60) {
                                    lineItemStatus = LineItemStatus.ORDER_RECEIVED;
                                } else if (statusChance < 75) {
                                    lineItemStatus = LineItemStatus.ON_HOLD;
                                } else {
                                    lineItemStatus = LineItemStatus.REJECT;
                                }

                                lineItem.setStatus(lineItemStatus);
                                lineItem.setStatusChangedAt(LocalDateTime.now().minusDays(random.nextInt(90)));

                                // For items with ORDER_PLACED or ORDER_RECEIVED status, add vendor and pricing
                                if (lineItemStatus == LineItemStatus.ORDER_PLACED || lineItemStatus == LineItemStatus.ORDER_RECEIVED) {
                                    Vendor vendor = vendors.get(random.nextInt(vendors.size()));
                                    lineItem.setVendor(vendor);

                                    // Varied price range based on material type
                                    int basePrice;
                                    if (material.getName().contains("Silicon") ||
                                            material.getName().contains("Circuit") ||
                                            material.getName().contains("Chip") ||
                                            material.getName().contains("Cell")) {
                                        basePrice = 100 + random.nextInt(1901); // 100-2000 for electronics
                                    } else if (material.getName().contains("Steel") ||
                                            material.getName().contains("Aluminum") ||
                                            material.getName().contains("Titanium") ||
                                            material.getName().contains("Carbon")) {
                                        basePrice = 50 + random.nextInt(451); // 50-500 for metals
                                    } else {
                                        basePrice = 10 + random.nextInt(991); // 10-1000 for others
                                    }
                                    lineItem.setUnitPrice(BigDecimal.valueOf(basePrice));

                                    // Ordered quantity might be different from requested
                                    int maxQty = lineItem.getRequestedQuantity().intValue();
                                    BigDecimal orderedQty;

                                    if (random.nextInt(10) < 7) {
                                        // 70% chance to get full requested quantity
                                        orderedQty = lineItem.getRequestedQuantity();
                                    } else {
                                        // 30% chance to get partial quantity
                                        orderedQty = BigDecimal.valueOf(
                                                1 + random.nextInt(Math.max(1, maxQty - 1)));
                                    }

                                    lineItem.setOrderedQuantity(orderedQty);

                                    BigDecimal totalValue = lineItem.getUnitPrice().multiply(orderedQty);
                                    lineItem.setTotalOrderValue(totalValue);

                                    // More varied remarks
                                    String[] purchaseRemarks = {
                                            "Procured from " + vendor.getName() + " at competitive price",
                                            "Special discount applied from " + vendor.getName(),
                                            "Rush order placed with " + vendor.getName(),
                                            "Quality verified by QA team from " + vendor.getName(),
                                            "Partial order placed with " + vendor.getName() + ", remainder on backorder",
                                            "Bulk pricing negotiated with " + vendor.getName()
                                    };

                                    lineItem.setPurchaseTeamRemarks(
                                            purchaseRemarks[random.nextInt(purchaseRemarks.length)]);

                                    // For received items, add factory remarks
                                    if (lineItemStatus == LineItemStatus.ORDER_RECEIVED) {
                                        String[] factoryRemarks = {
                                                "Received and verified",
                                                "Received with minor quality issues",
                                                "Received in good condition",
                                                "Partial delivery accepted",
                                                "Delivery delayed but accepted",
                                                "Required additional quality checks"
                                        };

                                        lineItem.setFactoryRemarks(
                                                factoryRemarks[random.nextInt(factoryRemarks.length)]);
                                    }
                                } else if (lineItemStatus == LineItemStatus.REJECT) {
                                    String[] rejectRemarks = {
                                            "Not available from suppliers",
                                            "Cost exceeds budget allocation",
                                            "Specifications unclear, need clarification",
                                            "Alternate material recommended",
                                            "Discontinued by manufacturer",
                                            "Quality standards cannot be met"
                                    };

                                    lineItem.setPurchaseTeamRemarks(
                                            rejectRemarks[random.nextInt(rejectRemarks.length)]);
                                } else if (lineItemStatus == LineItemStatus.ON_HOLD) {
                                    String[] holdRemarks = {
                                            "Waiting for budget approval",
                                            "Vendor selection in progress",
                                            "Technical specifications under review",
                                            "Awaiting import clearance",
                                            "Pending senior management approval",
                                            "Alternative sources being evaluated"
                                    };

                                    lineItem.setPurchaseTeamRemarks(
                                            holdRemarks[random.nextInt(holdRemarks.length)]);
                                } else if (lineItemStatus == LineItemStatus.VENDOR_DISCUSSION) {
                                    String[] discussionRemarks = {
                                            "Negotiating with suppliers",
                                            "Requesting samples for evaluation",
                                            "Comparing quotes from multiple vendors",
                                            "Discussing delivery timeline options",
                                            "Verifying material specifications with vendors",
                                            "Quality certification in progress"
                                    };

                                    lineItem.setPurchaseTeamRemarks(
                                            discussionRemarks[random.nextInt(discussionRemarks.length)]);
                                }
                            }

                            lineItemRepository.save(lineItem);
                            lineItemCount++;
                        } catch (Exception e) {
                            System.err.println("Error creating line item for request " +
                                    savedRequest.getUniqueIdentifier() + ": " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error creating procurement request for factory " +
                            factory.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Target was " + minRequests + " requests, created " + requestCount);
        return new int[]{requestCount, lineItemCount};
    }

    // Updated to handle empty lists and add logging
    private User getRandomUserWithFactory(List<User> users, Factory factory) {
        Random random = new Random();
        List<User> eligibleUsers = new ArrayList<>();

        for (User user : users) {
            if (user.getFactories() != null && user.getFactories().contains(factory)) {
                eligibleUsers.add(user);
            }
        }

        if (eligibleUsers.isEmpty()) {
            System.err.println("No eligible users found for factory: " + factory.getName());
            return null;
        }

        return eligibleUsers.get(random.nextInt(eligibleUsers.size()));
    }
}