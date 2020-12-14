package com.lingting.gzm.virtual.currency.contract;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 比特代币 合约地址
 *
 * @author lingting 2020-09-02 13:40
 */
@Getter
@AllArgsConstructor
public enum Omni implements Contract {

	/**
	 * 代币名称
	 */
	USDT("31"),

	BTC("0"),

	OMNI("1"),

	MAID_SAFE_COIN("3"),

	;

	/**
	 * 比特合约地址, 仅提供正式服
	 */
	private final String hash;

	/**
	 * 通过合约hash获取属于哪个 比特合约
	 *
	 * @author lingting 2020-09-02 13:44
	 */
	public static Omni getByHash(String hash) {
		if (StrUtil.isEmpty(hash)) {
			return null;
		}
		for (Omni c : values()) {
			if (c.hash.equalsIgnoreCase(hash)) {
				return c;
			}
		}
		return null;
	}

	public static Omni getById(Integer id) {
		return getByHash(id.toString());
	}

}