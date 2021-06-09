package top.hihanying.mall.admin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.hihanying.mall.mbg.mapper.PmsProductMapper;
import top.hihanying.mall.mbg.model.PmsProduct;
import top.hihanying.mall.mbg.model.PmsProductExample;
import top.hihanying.mall.admin.service.PmsProductService;

import java.util.List;

@Service
public class PmsProductServiceImpl implements PmsProductService {
    @Autowired
    private PmsProductMapper pmsProductMapper;

    @Override
    public List<PmsProduct> list(String keyword) {
        PmsProductExample productExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = productExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0);
        if(!StringUtils.isEmpty(keyword)){
            criteria.andNameLike("%" + keyword + "%");
            productExample.or().andDeleteStatusEqualTo(0).andProductSnLike("%" + keyword + "%");
        }
        return pmsProductMapper.selectByExample(productExample);
    }
}
