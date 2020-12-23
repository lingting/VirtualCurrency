package com.lingting.gzm.virtual.currency;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.web3j.crypto.Credentials;

/**
 * 虚拟货币账号
 *
 * @author lingting 2020/12/22 16:12
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class VirtualCurrencyAccount {

	/**
	 * 地址
	 */
	private String address;

	/**
	 * 公钥
	 */
	private String publicKey;

	/**
	 * 私钥
	 */
	private String privateKey;

	/**
	 * 证书
	 */
	private Credentials credentials;

	public VirtualCurrencyAccount(String address, String publicKey, String privateKey) {
		this.address = address;
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

}