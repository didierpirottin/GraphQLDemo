package org.thelidia.demo.utils;

import org.jooq.Condition;
import org.jooq.TableField;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.thelidia.demo.utils.Utils.mapIfNonNull;

public record StringFilter(
        String is,
        String startsWith,
        String contains,
        List<String> isOneOf) {

    public List<Condition> conditions(TableField field) {
        return Stream.of(
                        mapIfNonNull(is, is -> field.eq(is)),
                        mapIfNonNull(startsWith, startsWith -> field.startsWith(StringFilter.this.startsWith)),
                        mapIfNonNull(contains, contains -> field.contains(contains)),
                        mapIfNonNull(isOneOf, isOneOf -> field.in(StringFilter.this.isOneOf)))
                .filter(Objects::nonNull)
                .toList();
    }
}
