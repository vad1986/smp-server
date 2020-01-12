package com.ads;

import com.ads.dataObjects.Campaign;
import com.ads.dataObjects.MyPriotityQue;
import com.ads.dataObjects.Product;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

@org.springframework.stereotype.Service
public class Service {
    public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static int serial;
    private static String[] categories ;
    private static Map<String, MyPriotityQue<Product>> products; //Category vs Products (for campaign initialisation)
    private static TreeMap<String, MyPriotityQue<Campaign>> campaigns; // category vs campaigns prioritised by bid

    public Service() {
        if (products == null && campaigns == null) {
            categories=new String[]{"shoes", "beers", "electronics", "clothes", "cars", "food"};
            serial = 0;
            initProducts();
            campaigns = new TreeMap<>();
        }
    }

    /**
     * This method fills products into random categories selected from the categories array
     * for the purpose of this exercise we will insert 20 different products into these categories
     */
    private void initProducts() {
        products=new HashMap<>();
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            String category = categories[random.nextInt(categories.length - 1)];
            Product product = new Product(getRandomProductName(),category,getRandomPrice(),serial++);
            if (!products.containsKey(category)) {
                products.put(category, new MyPriotityQue<>());
            }
            products.get(category).add(product);
        }

    }

    /**
     * This method receives category and creates 5 random Products for this category
     * I could have returned a message saying "There are no products in this category" but I have decided
     * to make this flow as simple as possible
     *
     *
     * @param category
     *
     */
    private void addRandomProducts(String category){
        if(!products.containsKey(category)) {
            products.put(category, new MyPriotityQue<>());
            for (int i = 0; i < 5; i++) {
                Product product = new Product(getRandomProductName(),category,getRandomPrice(),serial++);
                products.get(category).add(product);
            }
        }
    }


    /**
     * Returns random price for the product
     *
     * @return
     */
    private double getRandomPrice() {
        Random random=new Random();
        double randomValue = 0.5 + (100 - 0.5) * random.nextDouble();
        return randomValue;
    }


    /**
     * Creates campaign
     * Checks if this campaign is already exists
     * checks if this category exists, if not, the method creates the new category
     * @param campaign
     * @return
     */
    public Campaign createCampaign(Campaign campaign) {
        addRandomProducts(campaign.getCategory());
        MyPriotityQue<Product> categoryProducts=products.get(campaign.getCategory());
        campaign.setProducts(categoryProducts);

        if(!campaigns.containsKey(campaign.getCategory())){
            campaigns.put(campaign.getCategory(),new MyPriotityQue<>());
        }

        campaigns.get(campaign.getCategory()).add(campaign);
        printCampaigns();
        return campaign;
    }


    /**
     * Returns a totally random product name with random characters
     * Could have made a list of names or pulled names from a foreign api.
     * @return
     */
    public String getRandomProductName() {
        Random random=new Random();
       String str="";
        for (int i = 0; i < 7; i++) {
            int num=random.nextInt(upper.length()-1);
            str+=upper.charAt(num);
        }
        return str;
    }


    /**
     * Finds the product with the biggest price in the Campaign with the higher bid.
     * @param category = looks in this category
     * @return
     */


    public Product getBestBidAdByCategory(String category) {
        printCampaigns();
        if(campaigns.containsKey(category)){
            Campaign bestCampaign= (Campaign) campaigns.get(category).peek();
            if(bestCampaign.isActive())
            return (Product) bestCampaign.getProducts().peek();
            else
                return getBestBidProduct();
        }else{
            return getBestBidProduct();
        }
    }


    /**
     * Searches for the highest bid Campaign through all the active Campaigns in all the Categories
     * Finds The product with the highes price
     * @return
     */
    private Product getBestBidProduct(){
        MyPriotityQue<Campaign>allCampaigns=new MyPriotityQue<>();
        campaigns.forEach((cat,camp)->{
            MyPriotityQue temp=camp;
            while(!temp.isEmpty()){
                Campaign cam=(Campaign) temp.poll();
                if(cam.isActive()){
                    allCampaigns.add(cam);
                    break;
                }
            }
        });
        Campaign bestCampaign= (Campaign)allCampaigns.peek();
        if(bestCampaign.isActive())
            return (Product) bestCampaign.getProducts().peek();
        else
            return null;
    }

    public void printCampaigns() {
        campaigns.forEach((category,camps)->{
            System.out.println("Category="+category+"\n[");
            for (Object camp : camps) {
                Campaign c=(Campaign) camp;
                System.out.println("Campaign="+c.getName());
                System.out.print("bid="+c.getBid());
                System.out.print(" startDate="+new SimpleDateFormat("dd.MM.yyyy").format(c.getStartDate().getTime()));
                System.out.print(" endDate="+new SimpleDateFormat("dd.MM.yyyy").format(c.getEndDate().getTime()));
                System.out.print(" numberProducts="+c.getProducts().size()+"\n[");

                c.getProducts().forEach(prod->{
                    System.out.println(prod.toString());
                });
                System.out.println("]");
            }
            for (int i = 0; i < camps.size(); i++) {
            }
            System.out.println("]");
        });
    }
}