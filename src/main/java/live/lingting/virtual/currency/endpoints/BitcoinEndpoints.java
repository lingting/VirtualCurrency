package live.lingting.virtual.currency.endpoints;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Omni节点
 *
 * @author lingting 2020-09-02 17:04
 */
@Getter
@AllArgsConstructor
public enum BitcoinEndpoints implements Endpoints {

	/**
	 * 主节点 <a href="https://www.blockchain.com/api/blockchain_api"/>
	 */
	MAINNET("https://blockchain.info/", "主节点"),

	/**
	 * 测试节点 <a href="https://www.blockchain.com/api/blockchain_api"/>
	 */
	TEST("https://testnet.blockchain.info/", "测试节点"),

	;

	private final String http;

	private final String desc;

}
