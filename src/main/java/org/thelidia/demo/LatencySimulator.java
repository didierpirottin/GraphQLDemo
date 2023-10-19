package org.thelidia.demo;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class LatencySimulator implements ExecuteListener {
    @Override
    @WithSpan
    public final void start(ExecuteContext ctx) {
        SleepHelper.sleep(10);
    }
}
