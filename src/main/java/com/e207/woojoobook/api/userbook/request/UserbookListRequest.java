package com.e207.woojoobook.api.userbook.request;

public record UserbookListRequest (String areaCode, String keyword, Long userbookId, int pageSize){
}
