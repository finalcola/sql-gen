# sql-gen

***mybatis代码生成***

## 示例代码

```
        Configuration configuration = new Configuration();
        configuration.setDriverClass("com.mysql.cj.jdbc.Driver")
                .setJdbcUrl("jdbc:mysql://localhost:3306/dbName?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC")
                .setUsername("user")
                .setPassword("password")
                .setDir("tmp/gen")
                .setPackageName("com.finalcola");
        SqlGen.generateSql(configuration);
```

## Configuration配置

// todo

