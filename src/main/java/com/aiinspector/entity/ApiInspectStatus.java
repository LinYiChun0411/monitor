package com.aiinspector.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import org.jboss.logging.FormatWith;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;

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
public class ApiInspectStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
     * PK(主鍵)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private BigInteger id;

    /**
     * success sum of count
     */
    private Integer successCount;

    /**
     *  fail sum of count
     */
    private Integer failCount;
    
    /**
     * inspect url
     */
    private String inspectUrl;

    
    /**
     * inspect date
     */
    @DateTimeFormat(pattern = "yyyyMMdd")
    private Date inspect_date;

    /**
     * last resp status
     */
    private Integer lastRespStatus;

    /**
     * create dateTime
     */
    private Date updateDatetime;
    /**
     * lock version
     */
    @Version
    private Integer version;
	
}
