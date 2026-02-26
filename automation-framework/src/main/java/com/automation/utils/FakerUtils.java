package com.automation.utils;

import net.datafaker.Faker;

public final class FakerUtils {

    private static final Faker FAKER = new Faker();

    private FakerUtils() {
    }

    public static Faker getFaker() {
        return FAKER;
    }

    public static String randomFirstName() {
        return FAKER.name().firstName();
    }

    public static String randomLastName() {
        return FAKER.name().lastName();
    }

    public static String randomFullName() {
        return FAKER.name().fullName();
    }

    public static String randomEmail() {
        return FAKER.internet().emailAddress();
    }

    @SuppressWarnings("removal")
    public static String randomPassword() {
        return FAKER.internet().password(8, 16, true, true, true);
    }

    public static String randomPhoneNumber() {
        return FAKER.phoneNumber().phoneNumber();
    }

    public static String randomAddress() {
        return FAKER.address().fullAddress();
    }

    public static String randomCity() {
        return FAKER.address().city();
    }

    public static String randomCompany() {
        return FAKER.company().name();
    }

    public static String randomJobTitle() {
        return FAKER.job().title();
    }

    public static int randomNumber(int min, int max) {
        return FAKER.number().numberBetween(min, max);
    }
}
