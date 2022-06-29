package com.blog.quark.id.generator;


public final class IdGenerators {
    
    private static final SnowflakeId SNOWFLAKEID = new SnowflakeId();
    
    private static final IdGenerator<SnowflakeId> SNOWFLAKE_ID_GENERATOR = new IdGenerator<SnowflakeId>() {
        @Override
        public long generateId() {
            return SNOWFLAKEID.nextId();
        }

        @Override
        public Class<SnowflakeId> getType() {
            return SnowflakeId.class;
        }
    };
    
    
    public static long getId() {
        return SNOWFLAKE_ID_GENERATOR.generateId();
    }
    
}
