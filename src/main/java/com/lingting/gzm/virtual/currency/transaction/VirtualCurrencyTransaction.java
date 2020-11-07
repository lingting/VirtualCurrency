package com.lingting.gzm.virtual.currency.transaction;

import com.lingting.gzm.virtual.currency.contract.Contract;
import com.lingting.gzm.virtual.currency.enums.Protocol;
import com.lingting.gzm.virtual.currency.enums.TransactionStatus;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author lingting 2020-09-02 14:02
 */
@Getter
@Setter
@Accessors(chain = true)
public class VirtualCurrencyTransaction {

	/**
	 * 交易号
	 */
	private String hash;

	/**
	 * 块号
	 */
	private BigInteger block;

	/**
	 * 块hash
	 */
	private String blockHash;

	/**
	 * 转账方
	 */
	private String from;

	/**
	 * 收账方
	 */
	private String to;

	/**
	 * 转账数量 单位 个
	 */
	private BigDecimal value;

	/**
	 * 交易状态
	 */
	private TransactionStatus status;

	/**
	 * 合约类型，可能为空
	 */
	private Contract contract;

	/**
	 * 协议
	 */
	private Protocol protocol;

	/**
	 * 交易时间, 时区 UTC
	 */
	private LocalDateTime time;

	/**
	 * 延迟时间, 单位: 分钟 如果订单创建时间与当前时间差小于设定时间,则交易状态为等待
	 */
	private long delay;

	public TransactionStatus getStatus() {
		if (getTime() != null && Duration.between(getTime(), LocalDateTime.now()).toMinutes() <= delay) {
			return TransactionStatus.WAIT;
		}
		return status;
	}

	public VirtualCurrencyTransaction setTime(LocalDateTime time) {
		this.time = time;
		return this;
	}

	public VirtualCurrencyTransaction setTime(long time) {
		return setTime(LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC));
	}

	/**
	 * 返回系统默认时区的交易时间
	 *
	 * @author lingting 2020-09-23 18:41
	 */
	public LocalDateTime getTime() {
		// 计算当前时区的偏移量 单位: 秒， 然后让时间偏移
		return getTimeByZone(ZoneId.systemDefault());
	}

	/**
	 * 返回UTC时区的交易时间
	 *
	 * @author lingting 2020-09-23 18:41
	 */
	public LocalDateTime getTimeByUtc() {
		return time;
	}

	/**
	 * 获取指定时区的交易时间
	 *
	 * @author lingting 2020-09-23 19:12
	 */
	public LocalDateTime getTimeByZone(ZoneId zoneId) {
		if (time == null) {
			return null;
		}
		return time.plusSeconds(TimeZone.getTimeZone(zoneId).getRawOffset() / 1000);
	}

	public long getDelay() {
		return delay > 0 ? delay : 0;
	}

}