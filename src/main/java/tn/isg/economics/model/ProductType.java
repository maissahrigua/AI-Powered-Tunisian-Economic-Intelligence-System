package tn.isg.economics.model;

public enum ProductType {
     OLIVE_OIL ("Huile d’olive" ) ,
     DATES ("Dattes" ) ,
     CITRUS_FRUITS ("Agrumes" ) ,
     WHEAT ("Blé" ) ,
     TOMATOES ("Tomates" ) ,
     PEPPERS ("Piments" ) ;
     private final String frenchName ;

     ProductType ( String frenchName ) {
      this . frenchName = frenchName ;
      }

      public String getFrenchName () {
         return frenchName ;
     }
}