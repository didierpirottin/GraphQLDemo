package org.thelidia.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Controller;
import org.thelidia.demo.model.AddressModel;
import org.thelidia.demo.model.CustomerModel;

import static jooq.generated.tables.Addresses.ADDRESSES;
import static org.thelidia.demo.mappers.AdressModelMapper.mapAddressRecordToModel;

@Controller
@RequiredArgsConstructor
public class CustomerAddressController {
    private final DSLContext dslContext;

    //    @SchemaMapping(typeName = "Customer")
    AddressModel address(CustomerModel customerModel) {
        return mapAddressRecordToModel(dslContext.selectFrom(ADDRESSES)
                .where(ADDRESSES.ID.eq(customerModel.addressId()))
                .fetchOne());
    }

}
