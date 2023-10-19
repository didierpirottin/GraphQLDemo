package org.thelidia.demo.mappers;

import org.jooq.Record;
import org.thelidia.demo.model.AccountModel;

import static jooq.generated.tables.Accounts.ACCOUNTS;

public class AccountModelMapper {
    public static AccountModel mapAccountRecordToModel(Record record) {
        if (record == null || record.field(ACCOUNTS.IBAN) == null) {
            return null;
        }
        return new AccountModel(
                record.get(ACCOUNTS.ID),
                record.get(ACCOUNTS.IBAN),
                record.get(ACCOUNTS.BALANCE),
                record.get(ACCOUNTS.CURRENCY),
                record.get(ACCOUNTS.CUSTOMER_ID)
        );
    }

}
