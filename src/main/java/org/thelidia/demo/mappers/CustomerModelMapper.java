package org.thelidia.demo.mappers;

import org.jooq.Record;
import org.jooq.Result;
import org.thelidia.demo.model.AccountModel;
import org.thelidia.demo.model.AddressModel;
import org.thelidia.demo.model.CustomerModel;

import java.util.Collections;
import java.util.List;

import static jooq.generated.tables.Customers.CUSTOMERS;
import static org.thelidia.demo.mappers.AdressModelMapper.mapAddressRecordToModel;

public class CustomerModelMapper {

    public static final String ACCOUNTS = "ACCOUNTS";

    public static CustomerModel mapCustomerRecordToModel(Record record) {
        if (record == null) {
            return null;
        }
        AddressModel customerAddressModel = mapAddressRecordToModel(record);
        List<AccountModel> accounts = Collections.emptyList();
        if (record.field(ACCOUNTS) != null) {
            Result<Record> accountsResult = (Result<Record>) record.get(ACCOUNTS);
            accounts = accountsResult.stream()
                    .map(AccountModelMapper::mapAccountRecordToModel)
                    .toList();
        }
        return new CustomerModel(
                record.get(CUSTOMERS.ID),
                record.get(CUSTOMERS.FIRST_NAME),
                record.get(CUSTOMERS.LAST_NAME),
                record.get(CUSTOMERS.ADDRESS_ID),
                customerAddressModel,
                accounts
        );
    }

}
