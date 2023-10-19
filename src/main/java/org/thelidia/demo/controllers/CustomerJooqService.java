package org.thelidia.demo.controllers;

import graphql.schema.DataFetchingFieldSelectionSet;
import jooq.generated.tables.records.AccountsRecord;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Service;
import org.thelidia.demo.model.CustomerFilter;

import static jooq.generated.tables.Accounts.ACCOUNTS;
import static jooq.generated.tables.Addresses.ADDRESSES;
import static jooq.generated.tables.Customers.CUSTOMERS;

@Service
@RequiredArgsConstructor
public class CustomerJooqService {
    private final DSLContext dslContext;

    SelectJoinStep<Record> buildCustomerQuery(@Argument CustomerFilter filter, DataFetchingFieldSelectionSet fieldSelectionSet) {
        SelectSelectStep<Record> select = buildSelectStep(fieldSelectionSet);
        SelectJoinStep<Record> query = buildFromAndJoinSteps(filter, fieldSelectionSet, select);
        query = applyWhereClause(filter, query);
        return query;
    }

    private SelectJoinStep<Record> applyWhereClause(CustomerFilter filter, SelectJoinStep<Record> query) {
        if (filter != null) {
            query = filter.applyOn(query);
        }
        return query;
    }

    private SelectJoinStep<Record> buildFromAndJoinSteps(CustomerFilter filter, DataFetchingFieldSelectionSet fieldSelectionSet, SelectSelectStep<Record> select) {
        @NotNull SelectJoinStep<Record> query = select.from(CUSTOMERS);
        if ((filter != null && filter.address() != null) ||
                fieldSelectionSet.contains("address")) {
            query = leftJoinAdress(query);
        }
        return query;
    }

    private SelectJoinStep<Record> innerJoinAdress(SelectJoinStep<Record> query) {
        return query.
                join(ADDRESSES).on(ADDRESSES.ID.eq(CUSTOMERS.ADDRESS_ID));
    }

    private @NotNull SelectOnConditionStep<Record> leftJoinAdress(SelectJoinStep<Record> query) {
        return query.
                leftJoin(ADDRESSES).on(ADDRESSES.ID.eq(CUSTOMERS.ADDRESS_ID));
    }

    private SelectSelectStep<Record> buildSelectStep(DataFetchingFieldSelectionSet fieldSelectionSet) {
        SelectSelectStep<Record> select = dslContext.select(CUSTOMERS.asterisk());
        if (fieldSelectionSet.contains("address")) {
            select = select.select(ADDRESSES.asterisk());
        }
        if (fieldSelectionSet.contains("accounts")) {
            select = select.select(buildSelectAccounts());
        }
        return select;
    }

    @NotNull
    private Field<Result<AccountsRecord>> buildSelectAccounts() {
        // TODO add filter on the account list
        return DSL.multiset(dslContext.selectFrom(ACCOUNTS).where(ACCOUNTS.CUSTOMER_ID.eq(CUSTOMERS.ID))).as("ACCOUNTS");
    }

}
