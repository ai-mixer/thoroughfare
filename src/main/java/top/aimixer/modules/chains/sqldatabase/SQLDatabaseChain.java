package top.aimixer.modules.chains.sqldatabase;

import top.aimixer.modules.chains.Chain;

import java.util.List;
import java.util.Map;

public class SQLDatabaseChain extends Chain {
    @Override
    public List<String> inputKeys() {
        return null;
    }

    @Override
    public List<String> outputKeys() {
        return null;
    }

    @Override
    public Map<String, String> call(Map<String, String> inputs) {
        return null;
    }

    @Override
    public String chainType() {
        return "sql_database_chain";
    }
}
