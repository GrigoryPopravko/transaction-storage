package pl.popravko.transactionstorage.util;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.function.Function;

@NoArgsConstructor(staticName = "builder")
public class Predicate {

    private BooleanExpression expression = Expressions.TRUE;

    public <V> Predicate add(V value, Function<V, BooleanExpression> function) {
        if (Objects.nonNull(value) && !isEmptyIfString(value)) {
            expression = expression.and(function.apply(value));
        }
        return this;
    }

    private <V> boolean isEmptyIfString(V value) {
        return value instanceof String && ((String) value).isEmpty();
    }

    public BooleanExpression build() {
        return expression;
    }
}
