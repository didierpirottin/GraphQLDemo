package org.thelidia.demo.mappers;

import jooq.generated.tables.Customers;
import org.jooq.Record;
import org.thelidia.demo.model.AddressModel;

import static jooq.generated.tables.Addresses.ADDRESSES;
import static jooq.generated.tables.Customers.CUSTOMERS;

public class AdressModelMapper {
    public static AddressModel mapAddressRecordToModel(Record record) {
        if (record == null ||
                record.field(ADDRESSES.STREET_NUMBER) == null ||
                record.get(CUSTOMERS.ADDRESS_ID) == null
        ) {
            return null;
        }
        return new AddressModel(
                record.get(ADDRESSES.STREET_NUMBER),
                record.get(ADDRESSES.STREET_NAME),
                record.get(ADDRESSES.ZIP_CODE),
                record.get(ADDRESSES.CITY),
                record.get(ADDRESSES.COUNTRY)
        );
    }

}
