package com.ads;

import com.ads.dataObjects.Campaign;
import com.ads.dataObjects.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {

    @Autowired
    private Service service;

    @RequestMapping(method = RequestMethod.POST,value = "/create-campaign")
    public Campaign createCampaign(@RequestBody Campaign campaign){
        return service.createCampaign(campaign);
    }

    @RequestMapping("/serve-ad/{category}")
    public Product serveAd(@PathVariable String category){
        return service.getBestBidAdByCategory(category);
    }
}
