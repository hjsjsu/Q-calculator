package com.hjsjsu.biz.precompute;

import cn.qmai.discount.core.model.common.DiscountWrapper;
import cn.qmai.discount.core.model.goods.GoodsInfo;
import cn.qmai.discount.core.model.goods.GoodsItem;
import cn.qmai.discount.core.precompute.PreCompute;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PreGroupBy implements PreCompute<GoodsItem> {
    /**
     * 判断符合条件的活动类型，符合才会执行preComputeItems
     */
    @Override
    public Set<String> matchTypes() {
        return Sets.newHashSet("zhekou");
    }

    /**
     * 对商品做一些复杂集合操作
     *
     * @param items      当前参与优惠的商品
     * @param discount   当前优惠
     * @param preCompute 存储计算的结果
     */
    @Override
    public void preComputeItems(List<GoodsItem> items, DiscountWrapper discount, Map<String, Object> preCompute) {
        preCompute.put("GroupBySkuIdPreCompute", items.stream().collect(
                Collectors.groupingBy(GoodsInfo::getSkuId,
                        Collectors.collectingAndThen(Collectors.toList(),
                                e -> e.stream().sorted().collect(Collectors.toList())))));
    }

}
