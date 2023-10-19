package org.thelidia.demo.mutation;

import jooq.generated.tables.Accounts;
import jooq.generated.tables.Addresses;
import jooq.generated.tables.Customers;
import jooq.generated.tables.records.AccountsRecord;
import jooq.generated.tables.records.AddressesRecord;
import jooq.generated.tables.records.CustomersRecord;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static jooq.generated.tables.Accounts.ACCOUNTS;
import static jooq.generated.tables.Addresses.ADDRESSES;
import static jooq.generated.tables.Customers.CUSTOMERS;
import static org.apache.commons.collections4.IteratorUtils.forEach;

@Controller
@RequiredArgsConstructor
public class MutationController {
    public static final Random RANDOM = new Random();
    private final DSLContext dslContext;
    private Faker faker = new Faker(Locale.US);
    private int accountIdCounter;

    @MutationMapping
    String resetDatabase() {
        deleteAll();
        int customerNumber = 10;
        int addressNumber = customerNumber / 2;
        insertAddresses(addressNumber);
        insertCustomers(customerNumber, addressNumber);
        insertAccounts(customerNumber);
        return "Done";
    }

    private void insertAccounts(int customerNumber) {
        List<AccountsRecord> accounts = IntStream.range(0, customerNumber)
                .mapToObj(i -> createAccounts(i))
                .flatMap(List::stream)
                .toList();
        dslContext.insertInto(ACCOUNTS, ACCOUNTS.fields())
                .valuesOfRecords(accounts)
                .execute();
    }

    private List<AccountsRecord> createAccounts(int i) {
        return Stream.generate(() -> createAccountForCustomer(i))
                .limit(RANDOM.nextInt(5))
                .toList();
    }

    private AccountsRecord createAccountForCustomer(int customerId) {
        return new AccountsRecord(
                String.valueOf(accountIdCounter++),
                faker.finance().iban(),
                1f * faker.random().nextInt(-100_000, 1_000_000),
                faker.money().currencyCode(),
                String.valueOf(customerId)
        );
    }

    private void insertAddresses(int size) {
        List<AddressesRecord> records = IntStream.range(0, size)
                .mapToObj(i -> createAdress(i))
                .toList();
        dslContext.insertInto(ADDRESSES, ADDRESSES.fields())
                .valuesOfRecords(records)
                .execute();
    }

    private AddressesRecord createAdress(int id) {
        return new AddressesRecord(
                String.valueOf(id),
                faker.address().streetName(),
                faker.address().streetAddressNumber(),
                faker.address().zipCode(),
                faker.address().country(),
                faker.address().city()
        );
    }

    private void insertCustomers(int size, int addressNumber) {
        List<CustomersRecord> records = IntStream.range(0, size)
                .mapToObj(i -> createCustomer(i, addressNumber))
                .toList();
        dslContext.insertInto(CUSTOMERS, CUSTOMERS.ID, CUSTOMERS.FIRST_NAME, CUSTOMERS.LAST_NAME, CUSTOMERS.ADDRESS_ID)
                .valuesOfRecords(records)
                .execute();
    }

    private CustomersRecord createCustomer(int id, int addressNumber) {
        faker = new Faker();
        return new CustomersRecord(
                String.valueOf(id),
                faker.name().firstName(),
                faker.name().lastName(),
                addressId(addressNumber)
        );
    }

    private String addressId(int addressNumber) {
        if (faker.random().nextBoolean()) {
            return null;
        }
        return String.valueOf(faker.random().nextInt(addressNumber));
    }

    @NotNull
    private static String randomId(int addressIdMax) {
        return String.valueOf(RANDOM.nextInt(addressIdMax));
    }

    private void deleteAll() {
        dslContext.deleteFrom(ACCOUNTS).execute();
        dslContext.deleteFrom(CUSTOMERS).execute();
        dslContext.deleteFrom(ADDRESSES).execute();
    }
}
