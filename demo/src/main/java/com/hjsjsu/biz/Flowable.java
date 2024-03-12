package com.hjsjsu.biz;

import cn.qmai.discount.core.model.common.CalcStage;
import cn.qmai.discount.core.model.common.CalcState;
import cn.qmai.discount.core.model.common.DiscountContext;
import cn.qmai.discount.core.model.goods.GoodsItem;
import cn.qmai.discount.core.permutation.Permutation;
import com.hjsjsu.biz.constant.Constant;

import java.util.List;

public class Flowable extends Permutation<GoodsItem> {

    /**
     * 相同最优解的处理逻辑，交给业务来实现
     *
     * @param curStages 当前的stage数组
     * @return
     */
    @Override
    protected boolean sameOptimumCondition(CalcStage[] curStages) {
        return false;
    }

    /**
     * context参数的重置逻辑，交给业务来实现
     *
     * @param context 上下文
     */
    @Override
    protected void resetContext(DiscountContext<GoodsItem> context) {

    }

    /**
     * 开启缓存的条件,如 a.size>4
     *
     * @param a 优惠排列
     */
    @Override
    protected boolean enableOptimize(List<Byte> a) {
        return false;
    }

    /**
     * 商品参数的重置逻辑，交给业务来实现
     *
     * @param item 单品
     */
    @Override
    protected void resetItems(GoodsItem item) {
        item.getExtra().put(Constant.UPDATEABLEPRICE, item.getSalePrice());
    }

    /**
     * 业务将状态记录到保存点
     *
     * @param state   保存点对象
     * @param context
     */
    @Override
    protected void makeSnapshot(CalcState<GoodsItem> state, DiscountContext<GoodsItem> context) {

    }

    /**
     * 业务返回保存点状态
     *
     * @param state   保存点对象
     * @param context
     */
    @Override
    protected void backToSnapshot(CalcState<GoodsItem> state, DiscountContext<GoodsItem> context) {

    }
}
