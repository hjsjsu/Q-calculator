package com.hjsjsu.biz.calc;

import cn.qmai.discount.core.discount.impl.AbstractCalculator;
import cn.qmai.discount.core.model.common.CalcStage;
import cn.qmai.discount.core.model.common.DiscountContext;
import cn.qmai.discount.core.model.common.DiscountWrapper;
import cn.qmai.discount.core.model.goods.GoodsItem;
import com.hjsjsu.biz.constant.Constant;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
@Component("zhekou")
@SuppressWarnings("unchecked")
public class ZhekouCalc extends AbstractCalculator<GoodsItem> {
    /**
     * 返回该优惠下的最终要支付的金额,若不符合则返回 prevStagePrice
     *
     * @param context         上下文
     * @param discountWrapper 优惠信息
     * @param records         记录享受过优惠的单品，key是calculateId，这里只提供容器，添加和判断规则由使用者自行决定
     * @param prevStagePrice  上一步计算出的订单的价格
     * @param curStage        当前stage
     * @return
     */
    @Override
    public long calc(DiscountContext<GoodsItem> context, DiscountWrapper discountWrapper, Map<Long, GoodsItem> records, long prevStagePrice, CalcStage curStage) {
        List<GoodsItem> goodsItems = Lists.newArrayList(context.getDiscountItemGroup().get(discountWrapper.getId()));
        Map<Long, List<GoodsItem>> map = (Map<Long, List<GoodsItem>>)context.getPreCompute().get("GroupBySkuIdPreCompute");
        long maxPrice = 0L;
        Collection<GoodsItem> group = CollectionUtils.EMPTY_COLLECTION;
        for (List<GoodsItem> value : map.values()) {
            long tmp = (long)value.iterator().next().getExtra().get(Constant.UPDATEABLEPRICE);
            if (tmp > maxPrice) {
                maxPrice = tmp;
                group = value;
            }
        }
        long discount = (long)(maxPrice * group.size() * 0.2);
        for (GoodsItem item : group) {
            item.getExtra().put(Constant.UPDATEABLEPRICE, (long)item.getExtra().get(Constant.UPDATEABLEPRICE) - discount / goodsItems.size());
        }

        return prevStagePrice - discount;
    }
}
