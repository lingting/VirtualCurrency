package com.lingting.gzm.virtual.currency.tronscan;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import com.lingting.gzm.virtual.currency.util.JsonUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lingting 2020/12/25 14:33
 */
@NoArgsConstructor
@Data
public class Transaction {

	public static Transaction of(HttpRequest request, Endpoints endpoints, String address)
			throws JsonProcessingException {
		// 固化块api, 仅能查询到已确认交易
		request.setUrl(endpoints.getHttpUrl("walletsolidity/gettransactionbyid"));
		request.body("{\"value\":\"" + address + "\",\"visible\":true}");
		return JsonUtil.toObj(request.execute().body(), Transaction.class);
	}

	@JsonProperty("txID")
	private String txId;

	@JsonProperty("raw_data")
	private RawData rawData;

	@JsonProperty("raw_data_hex")
	private String rawDataHex;

	@JsonProperty("ret")
	private List<Ret> ret;

	@JsonProperty("signature")
	private List<String> signature;

	@NoArgsConstructor
	@Data
	public static class RawData {

		@JsonProperty("data")
		private String data;

		@JsonProperty("ref_block_bytes")
		private String refBlockBytes;

		@JsonProperty("ref_block_hash")
		private String refBlockHash;

		@JsonProperty("expiration")
		private Long expiration;

		@JsonProperty("fee_limit")
		private BigInteger feeLimit;

		@JsonProperty("timestamp")
		private Long timestamp;

		@JsonProperty("contract")
		private List<Contract> contract;

		@NoArgsConstructor
		@Data
		public static class Contract {

			@JsonProperty("parameter")
			private Parameter parameter;

			@JsonProperty("type")
			private String type;

			@NoArgsConstructor
			@Data
			public static class Parameter {

				@JsonProperty("value")
				private Value value;

				@JsonProperty("type_url")
				private String typeUrl;

				@NoArgsConstructor
				@Data
				public static class Value {

					private String data;

					@JsonProperty("contract_address")
					private String contractAddress;

					@JsonProperty("amount")
					private BigDecimal amount;

					@JsonProperty("asset_name")
					private String assetName;

					@JsonProperty("owner_address")
					private String ownerAddress;

					@JsonProperty("to_address")
					private String toAddress;

				}

			}

		}

	}

	@NoArgsConstructor
	@Data
	public static class Ret {

		public static final String SUCCESS = "SUCCESS";

		/**
		 * 返回值如果与 {@link Ret#SUCCESS} 相同表示已成功
		 */
		@JsonProperty("contractRet")
		private String contractRet;

	}

}
