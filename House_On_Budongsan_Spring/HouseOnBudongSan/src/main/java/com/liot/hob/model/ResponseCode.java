package com.liot.hob.model;

// ResponseEntity에 담을 객체
public class ResponseCode<T> {
	private final String code;
	private final T items;
	
	public static class builder<T> {
		private String code;
		private T items;
		
		public builder() {}
		
		public builder<T> code(String val) {
			code = val;
			return this;
		}
		public builder<T> items(T val) {
			items = val;
			return this;
		}
		
		public ResponseCode<T> build() {
			return new ResponseCode<T>(this);
		}
	}
	
	private ResponseCode(builder<T> builder) {
		code = builder.code;
		items = builder.items;
	}
	
	public String getCode() {
		return this.code;
	}
	public T getItems() {
		return this.items;
	}

	@Override
	public String toString() {
		return "ResponseCode [code=" + code + ", items=" + items + "]";
	}

}
