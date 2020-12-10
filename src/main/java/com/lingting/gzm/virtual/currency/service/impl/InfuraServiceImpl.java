package com.lingting.gzm.virtual.currency.service.impl;

import cn.hutool.core.convert.Convert;
import com.lingting.gzm.virtual.currency.contract.Etherscan;
import com.lingting.gzm.virtual.currency.enums.EtherscanReceiptStatus;
import com.lingting.gzm.virtual.currency.enums.VcPlatform;
import com.lingting.gzm.virtual.currency.enums.TransactionStatus;
import com.lingting.gzm.virtual.currency.exception.TransactionException;
import com.lingting.gzm.virtual.currency.properties.InfuraProperties;
import com.lingting.gzm.virtual.currency.service.VirtualCurrencyService;
import com.lingting.gzm.virtual.currency.transaction.VirtualCurrencyTransaction;
import com.lingting.gzm.virtual.currency.util.EtherscanUtil;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

/**
 * @author lingting 2020-09-01 17:16
 */
@Slf4j
public class InfuraServiceImpl implements VirtualCurrencyService {

	private static final String INPUT_EMPTY = "0x";

	@Getter
	private final Web3j web3j;

	private final InfuraProperties properties;

	public InfuraServiceImpl(InfuraProperties properties) {
		this.properties = properties;
		// 使用web3j连接infura客户端
		web3j = Web3j.build(properties.getHttpService());
	}

	@Override
	@SneakyThrows
	public Optional<VirtualCurrencyTransaction> getTransactionByHash(String hash) {
		EthTransaction ethTransaction = web3j.ethGetTransactionByHash(hash).sendAsync().get();

		Optional<Transaction> optional;
		if (ethTransaction.hasError()) {
			// 订单出错
			log.error("查询eth订单出错: code: {}, message:{}", ethTransaction.getError().getCode(),
					ethTransaction.getError().getMessage());
			optional = Optional.empty();
		}
		else {
			// 订单没出错
			optional = ethTransaction.getTransaction();
		}

		/*
		 * 订单信息为空 如果交易还没有被打包，就查询不到交易信息
		 */
		if (!optional.isPresent()) {
			return Optional.empty();
		}
		Transaction transaction = optional.get();

		// 获取合约代币
		Etherscan contract = Etherscan.getByHash(transaction.getTo());
		// 解析input数据
		EtherscanUtil.Input input;
		if (INPUT_EMPTY.equals(transaction.getInput())) {
			// 不是使用代币交易，而是直接使用eth交易
			if (contract != null) {
				// 如果合约代币不为null
				throw new TransactionException("合约代币应该为null，但是解析出来的不为null，请检查. 交易hash: " + hash);
			}
			input = new EtherscanUtil.Input().setTo(transaction.getTo())
					.setValue(new BigDecimal(transaction.getValue()).divide(EtherscanUtil.ETH, MathContext.UNLIMITED));
		}
		else {
			input = EtherscanUtil.resolveInput(transaction.getInput());
		}

		VirtualCurrencyTransaction virtualCurrencyTransaction = new VirtualCurrencyTransaction()

				.setVcPlatform(VcPlatform.ETHERSCAN).setBlockHash(transaction.getBlockHash())

				.setBlock(transaction.getBlockNumber()).setHash(transaction.getHash()).setFrom(transaction.getFrom())

				.setTo(input.getTo())
				// 设置代币类型, input 中的优先
				.setContract(input.getContract() == null ? contract : input.getContract())
				// 设置金额
				.setValue(input.getValue())
				// 设置 input data
				.setInput(input);

		// 获取交易状态
		Optional<TransactionReceipt> receiptOptional = web3j.ethGetTransactionReceipt(hash).send()
				.getTransactionReceipt();
		if (receiptOptional.isPresent()
				&& receiptOptional.get().getStatus().equals(EtherscanReceiptStatus.SUCCESS.getValue())) {
			// 交易成功
			virtualCurrencyTransaction.setStatus(TransactionStatus.SUCCESS);
		}
		else {
			virtualCurrencyTransaction.setStatus(TransactionStatus.FAIL);
		}

		// 获取交易时间
		EthBlock block = web3j.ethGetBlockByHash(transaction.getBlockHash(), false).sendAsync().get();

		// 从平台获取的交易是属于 UTC 时区的
		return Optional.of(virtualCurrencyTransaction.setTime(
				LocalDateTime.ofEpochSecond(Convert.toLong(block.getBlock().getTimestamp()), 0, ZoneOffset.UTC)));
	}

}
