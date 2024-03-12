package com.hjsjsu.biz;

import cn.qmai.discount.core.aware.CalculatorRouter;
import cn.qmai.discount.core.enums.GroupRelation;
import cn.qmai.discount.core.model.common.*;
import cn.qmai.discount.core.model.goods.GoodsInfo;
import cn.qmai.discount.core.model.goods.GoodsItem;
import cn.qmai.discount.core.utils.DiscountGroupUtil;
import cn.qmai.discount.core.utils.IdGenerator;
import cn.qmai.discount.core.utils.LimitingUtil;
import com.hjsjsu.biz.constant.Constant;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class TestController {
    private final CalculatorRouter calculatorRouter;
    public TestController(CalculatorRouter calculatorRouter) {
        this.calculatorRouter = calculatorRouter;
    }

    @RequestMapping("test")
    @ResponseBody
    public Object test() {
        List<GoodsItem> goodsItems = mockItems();
        List<Pair<Set<DiscountWrapper>, Set<DiscountWrapper>>> pairs = transform(mockGroups());
        List<CalcStage> globalStages = Lists.newArrayList();
        int count = 0;
        long totalPrice = goodsItems.stream().mapToLong(GoodsInfo::getSalePrice).sum();
        long globalPrice = totalPrice;

        Flowable flowable = (Flowable)new Flowable().build(calculatorRouter);
        for (Pair<Set<DiscountWrapper>, Set<DiscountWrapper>> set: pairs) {
            count += LimitingUtil.count(set.getLeft().size());
            if (count > 100000) {
                break;
            }
            ArrayList<DiscountWrapper> wrappers = Lists.newArrayList(set.getLeft());
            DiscountContext<GoodsItem> ctx = DiscountContext.create(totalPrice, Lists.newArrayList(goodsItems), wrappers);
            flowable.perm(ctx);
            if (ctx.getCalcResult().getFinalPrice() < globalPrice) {
                globalStages = Arrays.asList(ctx.getCalcResult().getStages());
                globalPrice = ctx.getCalcResult().getFinalPrice();
            }
        }
        return Pair.of(globalPrice, globalStages);
    }

    private List<GoodsItem> mockItems() {
        IdGenerator idGenerator = IdGenerator.getInstance();

        GoodsInfo goodsInfo = GoodsInfo.of(6666L, 7777L, null, 4, 20 * 100, "产品1", null);
        GoodsInfo goodsInfo2 = GoodsInfo.of(6666L, 7778L, null, 2, 20 * 100, "产品2", null);
        List<GoodsItem> goodsItems = GoodsItem.generateItems(goodsInfo, idGenerator, x -> x.getExtra().put(Constant.UPDATEABLEPRICE, x.getSalePrice()));
        goodsItems.addAll(GoodsItem.generateItems(goodsInfo2, idGenerator, x -> x.getExtra().put(Constant.UPDATEABLEPRICE, x.getSalePrice())));
        return goodsItems;

    }
    private List<List<DiscountGroup>> mockGroups() {
        List<List<DiscountGroup>> groups = Lists.newArrayList();
        DiscountGroup discountGroup = new DiscountGroup();
        discountGroup.setRelation(GroupRelation.SHARE.getType());
        discountGroup.setItems(Lists.newArrayList(new Item("zhekou", "1"), new Item("manjian", "2"), new Item("manzeng", "3")));
        groups.add(Lists.newArrayList(discountGroup));
        return groups;
    }
    private List<Pair<Set<DiscountWrapper>, Set<DiscountWrapper>>> transform(List<List<DiscountGroup>> groups) {
        ArrayList<DiscountWrapper> wrapperList = Lists.newArrayList(
                DiscountWrapper.of("zhekou", "1", "折扣", false, new DiscountConfig()),
                DiscountWrapper.of("manjian", "2", "满减", false, new DiscountConfig())
        );
        Map<String, Map<String, DiscountWrapper>> inMap = wrapperList.stream().collect(Collectors.toMap(DiscountWrapper::getType, x -> ImmutableMap.of(x.getId(), x)));
        return DiscountGroupUtil.transform(groups, inMap);
    }
}
