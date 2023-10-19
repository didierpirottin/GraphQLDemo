package org.thelidia.demo.model;

import java.util.List;

public record CustomerModel(
        String id,
        String firstName,
        String lastName,
        String addressId,
        AddressModel address,
        List<AccountModel> accounts
) {
}
