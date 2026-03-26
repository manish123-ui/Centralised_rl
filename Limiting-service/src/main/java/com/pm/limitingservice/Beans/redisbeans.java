package com.pm.limitingservice.Beans;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Component
public class redisbeans {
    @Bean
    public DefaultRedisScript<Long> rateLimiterScript() {

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();

        script.setScriptText(
                "local key = KEYS[1] " +

                        "local bucketSize = tonumber(ARGV[1]) " +
                        "local refillRate = tonumber(ARGV[2]) " +
                        "local now = tonumber(ARGV[3]) " +

                        "local data = redis.call('GET', key) " +

                        "local tokens " +
                        "local lastRefill " +

                        "if data then " +
                        " local parsed = cjson.decode(data) " +
                        " tokens = parsed.tokens " +
                        " lastRefill = parsed.lastRefill " +
                        "else " +
                        " tokens = bucketSize " +
                        " lastRefill = now " +
                        "end " +

                        "local elapsed = now - lastRefill " +
                        "local tokensToAdd = math.floor(elapsed * refillRate / 1000) " +

                        "if tokensToAdd > 0 then " +
                        " tokens = math.min(bucketSize, tokens + tokensToAdd) " +
                        " lastRefill = now " +
                        "end " +

                        "if tokens <= 0 then return -1 end " +

                        "tokens = tokens - 1 " +

                        "local newData = cjson.encode({tokens=tokens,lastRefill=lastRefill}) " +

                        // TTL added (IMPORTANT)
                        "redis.call('SET', key, newData, 'PX', 60000) " +

                        "return tokens"
        );

        script.setResultType(Long.class);
        return script;
    }
}
