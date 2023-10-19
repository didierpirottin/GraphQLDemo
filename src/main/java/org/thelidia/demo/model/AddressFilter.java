package org.thelidia.demo.model;

import jooq.generated.tables.Addresses;
import net.datafaker.providers.base.Bool;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.thelidia.demo.utils.StringFilter;

import static jooq.generated.tables.Customers.CUSTOMERS;
import static org.thelidia.demo.utils.Utils.ifNonNull;

public record AddressFilter(
        Boolean exists,
        StringFilter streetNumber,
        StringFilter streetName,
        StringFilter zipCode,
        StringFilter city,
        StringFilter country
) {
    public SelectJoinStep<Record> applyOn(SelectJoinStep<Record> query) {
        ifNonNull(exists, exist -> query.where(
                exists? CUSTOMERS.ADDRESS_ID.isNotNull():CUSTOMERS.ADDRESS_ID.isNull()));
        ifNonNull(streetNumber, streetNumber -> query.where(streetNumber.conditions(Addresses.ADDRESSES.STREET_NUMBER)));
        ifNonNull(streetName, streetName -> query.where(streetName.conditions(Addresses.ADDRESSES.STREET_NAME)));
        ifNonNull(zipCode, zipCode -> query.where(zipCode.conditions(Addresses.ADDRESSES.ZIP_CODE)));
        ifNonNull(city, city -> query.where(city.conditions(Addresses.ADDRESSES.CITY)));
        ifNonNull(country, country -> query.where(country.conditions(Addresses.ADDRESSES.COUNTRY)));
        return query;
    }
}
