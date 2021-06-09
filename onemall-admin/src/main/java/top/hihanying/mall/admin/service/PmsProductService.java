package top.hihanying.mall.admin.service;

import top.hihanying.mall.mbg.model.PmsProduct;

import java.util.List;

public interface PmsProductService {
    // 根据商品名称或者货号模糊查询
    List<PmsProduct> list(String keyword);
}
