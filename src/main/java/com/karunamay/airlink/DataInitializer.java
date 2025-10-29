package com.karunamay.airlink;

import com.karunamay.airlink.model.flight.*;
import com.karunamay.airlink.model.user.Permission;
import com.karunamay.airlink.model.user.Role;
import com.karunamay.airlink.model.user.User;
import com.karunamay.airlink.repository.flight.*;
import com.karunamay.airlink.repository.user.PermissionRepository;
import com.karunamay.airlink.repository.user.RoleRepository;
import com.karunamay.airlink.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final SeatRepository seatRepository;
    private final AircraftRepository aircraftRepository;
    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;
    private final AirlineRepository airlineRepository;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing application data...");

//        initializePermissions();
//
//        initializeRoles();
//
        initializeAdminUser();
//
//        initializeAirlines();
//
//        initializeAirports();
//
//        initializeAircrafts();
//
//        initializeFlights();
//
//        initializeSeats();

        log.info("Application data initialized successfully");
    }

    private void initializeSeats() {
        if (seatRepository.count() == 0) {
            log.info("Creating default seat data...");

            // Fetch required Flights
            Flight flightAI101 = flightRepository.findByFlightNo("AI-101").orElse(null);
            Flight flight6E505 = flightRepository.findByFlightNo("6E-505").orElse(null);
            Flight flightAI202 = flightRepository.findByFlightNo("AI-202").orElse(null);

            if (flightAI101 == null || flight6E505 == null || flightAI202 == null) {
                log.warn("Skipping seat initialization: Required Flights (AI-101, 6E-505, AI-202) not found.");
                return;
            }

            List<Seat> seats = List.of(
                    // Seats for AI-101 (DEL -> BOM) - Airbus A320 (180 capacity)
                    Seat.builder().flight(flightAI101).seatNo("1A").seatClass(SeatClass.BUSINESS).available(true).priceModifier(new BigDecimal("1.80")).build(),
                    Seat.builder().flight(flightAI101).seatNo("1B").seatClass(SeatClass.BUSINESS).available(true).priceModifier(new BigDecimal("1.80")).build(),
                    Seat.builder().flight(flightAI101).seatNo("1C").seatClass(SeatClass.BUSINESS).available(false).priceModifier(new BigDecimal("1.80")).build(), // Example booked seat
                    Seat.builder().flight(flightAI101).seatNo("10A").seatClass(SeatClass.ECONOMY).available(true).priceModifier(new BigDecimal("1.00")).build(),
                    Seat.builder().flight(flightAI101).seatNo("10B").seatClass(SeatClass.ECONOMY).available(true).priceModifier(new BigDecimal("1.00")).build(),
                    Seat.builder().flight(flightAI101).seatNo("11A").seatClass(SeatClass.ECONOMY).available(true).priceModifier(new BigDecimal("1.00")).build(),

                    // Seats for 6E-505 (BOM -> BLR) - Boeing 737-800 (162 capacity)
                    Seat.builder().flight(flight6E505).seatNo("5D").seatClass(SeatClass.PREMIUM_ECONOMY).available(true).priceModifier(new BigDecimal("1.30")).build(),
                    Seat.builder().flight(flight6E505).seatNo("5E").seatClass(SeatClass.PREMIUM_ECONOMY).available(true).priceModifier(new BigDecimal("1.30")).build(),
                    Seat.builder().flight(flight6E505).seatNo("15A").seatClass(SeatClass.ECONOMY).available(true).priceModifier(new BigDecimal("1.00")).build(),
                    Seat.builder().flight(flight6E505).seatNo("15F").seatClass(SeatClass.ECONOMY).available(true).priceModifier(new BigDecimal("1.00")).build(),

                    // Seats for AI-202 (BLR -> DEL) - Boeing 787 Dreamliner (250 capacity)
                    Seat.builder().flight(flightAI202).seatNo("2A").seatClass(SeatClass.FIRST).available(true).priceModifier(new BigDecimal("2.50")).build(),
                    Seat.builder().flight(flightAI202).seatNo("2B").seatClass(SeatClass.FIRST).available(true).priceModifier(new BigDecimal("2.50")).build(),
                    Seat.builder().flight(flightAI202).seatNo("7A").seatClass(SeatClass.BUSINESS).available(true).priceModifier(new BigDecimal("1.90")).build(),
                    Seat.builder().flight(flightAI202).seatNo("20C").seatClass(SeatClass.ECONOMY).available(true).priceModifier(new BigDecimal("1.00")).build()
            );

            seatRepository.saveAll(seats);
            log.info("Initialized {} sample seats.", seats.size());
        } else {
            log.info("Seats already exist. Skipping initialization.");
        }
    }

    private void initializeAircrafts() {
        if (aircraftRepository.count() == 0) {
            log.info("Creating default aircraft data...");

            // Fetch required Airlines
            Airline airIndia = airlineRepository.findByCodeIgnoreCase("AI").orElse(null);
            Airline indigo = airlineRepository.findByCodeIgnoreCase("6E").orElse(null);

            if (airIndia == null || indigo == null) {
                log.warn("Skipping aircraft initialization: Required Airlines (AI, 6E) not found.");
                return;
            }

            List<Aircraft> aircrafts = List.of(
                    Aircraft.builder()
                            .model("Airbus A320")
                            .registrationNumber("VT-AI1") // Using correct field name
                            .capacity(180) // Using correct field name
                            .airline(airIndia)
                            .active(true)
                            .build(),
                    Aircraft.builder()
                            .model("Boeing 737-800")
                            .registrationNumber("VT-IN2") // Using correct field name
                            .capacity(162) // Using correct field name
                            .airline(indigo)
                            .active(true)
                            .build(),
                    Aircraft.builder()
                            .model("Boeing 787 Dreamliner")
                            .registrationNumber("VT-AI3") // Using correct field name
                            .capacity(250) // Using correct field name
                            .airline(airIndia)
                            .active(true)
                            .build()
            );

            aircraftRepository.saveAll(aircrafts);
            log.info("Initialized {} sample aircrafts.", aircrafts.size());
        } else {
            log.info("Aircrafts already exist. Skipping initialization.");
        }
    }

    private void initializeFlights() {
        if (flightRepository.count() == 0 && airlineRepository.count() > 0 && airportRepository.count() > 0 && aircraftRepository.count() > 0) {
            log.info("Creating default flight data...");

            Airline airIndia = airlineRepository.findByCodeIgnoreCase("AI").orElse(null);
            Airline indigo = airlineRepository.findByCodeIgnoreCase("6E").orElse(null);

            Airport del = airportRepository.findByCodeIgnoreCase("DEL").orElse(null);
            Airport bom = airportRepository.findByCodeIgnoreCase("BOM").orElse(null);
            Airport blr = airportRepository.findByCodeIgnoreCase("BLR").orElse(null);

            Aircraft a320 = aircraftRepository.findByRegistrationNumberIgnoreCase("VT-AI1").orElse(null);
            Aircraft b738 = aircraftRepository.findByRegistrationNumberIgnoreCase("VT-IN2").orElse(null);
            Aircraft b787 = aircraftRepository.findByRegistrationNumberIgnoreCase("VT-AI3").orElse(null);

            if (airIndia == null || indigo == null || del == null || bom == null || blr == null || a320 == null || b738 == null || b787 == null) {
                log.warn("Skipping flight initialization: Required Airlines, Airports, or Aircraft not found.");
                return;
            }

            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusDays(1);

            List<Flight> flights = List.of(
                    Flight.builder()
                            .flightNo("AI-101")
                            .airline(airIndia)
                            .aircraft(a320)
                            .srcAirport(del)
                            .destAirport(bom)
                            .departureTime(now.plusHours(1))
                            .arrivalTime(now.plusHours(3))
                            .basePrice(new BigDecimal("5500.00"))
                            .status(FlightStatus.SCHEDULED)
                            .build(),

                    Flight.builder()
                            .flightNo("6E-505")
                            .airline(indigo)
                            .aircraft(b738)
                            .srcAirport(bom)
                            .destAirport(blr)
                            .departureTime(now.plusHours(5))
                            .arrivalTime(now.plusHours(7).plusMinutes(30))
                            .basePrice(new BigDecimal("4200.00"))
                            .status(FlightStatus.SCHEDULED)
                            .build(),

                    Flight.builder()
                            .flightNo("AI-202")
                            .airline(airIndia)
                            .aircraft(b787)
                            .srcAirport(blr)
                            .destAirport(del)
                            .departureTime(now.plusDays(2).plusHours(9))
                            .arrivalTime(now.plusDays(2).plusHours(12))
                            .basePrice(new BigDecimal("8000.00"))
                            .status(FlightStatus.SCHEDULED)
                            .build(),

                    Flight.builder()
                            .flightNo("AI-302")
                            .airline(airIndia)
                            .aircraft(b787)
                            .srcAirport(blr)
                            .destAirport(del)
                            .departureTime(now.plusDays(2).plusHours(9))
                            .arrivalTime(now.plusDays(2).plusHours(12))
                            .basePrice(new BigDecimal("3000.00"))
                            .status(FlightStatus.SCHEDULED)
                            .build()

            );

            flightRepository.saveAll(flights);
            log.info("Initialized {} sample flights.", flights.size());
        } else {
            log.info("Flights already exist or dependencies are missing. Skipping flight data initialization.");
        }
    }

    private void initializeAirports() {
        if (airportRepository.count() == 0) {
            log.info("Creating default airport data...");

            List<Airport> airports = List.of(
                    Airport.builder().code("DEL").name("Indira Gandhi Intl Airport").city("New Delhi").country("India").active(true).build(),
                    Airport.builder().code("BOM").name("Chhatrapati Shivaji Maharaj Intl").city("Mumbai").country("India").active(true).build(),
                    Airport.builder().code("BLR").name("Kempegowda International Airport").city("Bengaluru").country("India").active(true).build(),
                    Airport.builder().code("DXB").name("Dubai International Airport").city("Dubai").country("United Arab Emirates").active(true).build(),
                    Airport.builder().code("CCU").name("Netaji Subhas Chandra Bose Intl").city("Kolkata").country("India").active(false).build()
            );

            airportRepository.saveAll(airports);
            log.info("Initialized {} sample airports.", airports.size());
        } else {
            log.info("Airports already exist. Skipping initialization.");
        }
    }

    private void initializeAirlines() {
        if (airlineRepository.count() == 0) {
            log.info("Creating default airline data...");

            List<Airline> airlines = List.of(
                    Airline.builder().code("AI").name("Air India").logoUrl("").active(true).build(),
                    Airline.builder().code("6E").name("IndiGo").logoUrl("").active(true).build(),
                    Airline.builder().code("UK").name("Vistara").logoUrl("").active(true).build(),
                    Airline.builder().code("G8").name("Go First").logoUrl("").active(false).build(),
                    Airline.builder().code("EK").name("Emirates").country("UAE").logoUrl("").active(true).build()
            );

            airlineRepository.saveAll(airlines);
            log.info("Initialized {} sample airlines.", airlines.size());
        } else {
            log.info("Airlines already exist. Skipping initialization.");
        }
    }

    private void initializePermissions() {
        if (permissionRepository.count() == 0) {
            log.info("Creating default permissions...");

            List<Permission> permissions = Arrays.asList(
                    createPermission("user:create", "Create new users", "user", "create"),
                    createPermission("user:read", "Read user information", "user", "read"),
                    createPermission("user:update", "Update user information", "user", "update"),
                    createPermission("user:delete", "Delete users", "user", "delete"),

                    createPermission("role:create", "Create new roles", "role", "create"),
                    createPermission("role:read", "Read role information", "role", "read"),
                    createPermission("role:update", "Update role information", "role", "update"),
                    createPermission("role:delete", "Delete roles", "role", "delete"),

                    createPermission("permission:create", "Create new permissions", "permission", "create"),
                    createPermission("permission:read", "Read permission information", "permission", "read"),
                    createPermission("permission:update", "Update permission information", "permission", "update"),
                    createPermission("permission:delete", "Delete permissions", "permission", "delete")
            );

            permissionRepository.saveAll(permissions);
            log.info("Created {} permissions", permissions.size());
        }
    }

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            log.info("Creating default roles...");

            List<Permission> allPermissions = permissionRepository.findAll();
            Set<Permission> userReadPermissions = permissionRepository.findByNameIn(Set.of(
                    "user:read", "user:create", "user:update"
            ));

            Role adminRole = Role.builder()
                    .name("ROLE_ADMIN")
                    .description("Administrator with full access")
                    .active(true)
                    .systemRole(true)
                    .permissions(new HashSet<>(allPermissions))
                    .build();

            Role userRole = Role.builder()
                    .name("ROLE_USER")
                    .description("Regular user with basic access")
                    .active(true)
                    .systemRole(true)
                    .permissions(new HashSet<>(userReadPermissions))
                    .build();

            Role moderatorRole = Role.builder()
                    .name("ROLE_MODERATOR")
                    .description("Moderator with elevated access")
                    .active(true)
                    .systemRole(false)
                    .permissions(new HashSet<>())
                    .build();

            roleRepository.saveAll(Arrays.asList(adminRole, userRole, moderatorRole));
            log.info("Created default roles: ADMIN, USER, MODERATOR");
        }
    }

    private void initializeAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            log.info("Creating default admin user...");

            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .firstName("System")
                    .lastName("Administrator")
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .emailVerified(true)
                    .roles(new HashSet<>())
                    .build();

            admin.addRole(adminRole);
            userRepository.save(admin);

            log.info("Default admin user created - Username: admin, Password: Admin@123");
            log.warn("IMPORTANT: Change the default admin password immediately!");
        }
    }

    private Permission createPermission(String name, String description, String resource, String action) {
        return Permission.builder()
                .name(name)
                .description(description)
                .resource(resource)
                .action(action)
                .active(true)
                .build();
    }
}
