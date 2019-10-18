package com.aiinspector.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ApiInspectFailLog implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
     * PK(主鍵)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private BigInteger id;

    /**
     * inspect URL
     */
    private String reqUrl;

    /**
          *  請求Class
     */
    private String reqClass;
    
    /**
         * 請求Method
     */
    private String reqMethod;

    /**
         * 請求Argument
     */
    private String reqArgument;

   /**
    * 失敗訊息
    */
    private String failMsg;
    
    /**
     *exception stackTrace
     */
    private String stackTrace;
        
    /**
     * update dateTime
     */
    private Date updateDatetime;

}
