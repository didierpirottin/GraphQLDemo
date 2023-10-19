package org.thelidia.demo.model;

import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.thelidia.demo.utils.StringFilter;

import static jooq.generated.tables.Customers.CUSTOMERS;
import static org.thelidia.demo.utils.Utils.ifNonNull;

public record CustomerFilter(
        StringFilter firstName,
        StringFilter lastName,
        AddressFilter address
        ) {
    public SelectJoinStep<Record> applyOn(SelectJoinStep<Record> query) {
        ifNonNull(firstName, firstName -> query.where(firstName.conditions(CUSTOMERS.FIRST_NAME)));
        ifNonNull(lastName, lastName -> query.where(CustomerFilter.this.lastName.conditions(CUSTOMERS.LAST_NAME)));
        ifNonNull(address, addressFilter -> addressFilter.applyOn(query));
        return query;
    }
}
