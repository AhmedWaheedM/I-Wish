module iwish.common {
    exports dtos;
    exports models;
    requires java.sql;
    
    // Export all DTO subpackages recursively
    exports dtos.requestDtos.contributionHandler;
    exports dtos.requestDtos.friendsHandler;

    exports dtos.requestDtos.Item; // Case sensitive! In code it is used as dtos.requestDtos.Item
    exports dtos.requestDtos.notificationHandler;
    exports dtos.requestDtos.userHandler;
    exports dtos.requestDtos.wishListHandler;
    exports dtos.requestDtos.wishListItemHandler;
}
