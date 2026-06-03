package com.example.qlqa.data.local.model;

import androidx.room.Embedded;
import androidx.room.Relation;
import com.example.qlqa.data.local.entities.Order;
import com.example.qlqa.data.local.entities.OrderDetail;
import com.example.qlqa.data.local.entities.Table;
import com.example.qlqa.data.local.entities.MenuItem;
import java.util.List;

public class OrderWithDetails {
    @Embedded
    public Order order;

    @Relation(
        parentColumn = "tableId",
        entityColumn = "tableId"
    )
    public Table table;

    @Relation(
        entity = OrderDetail.class,
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    public List<OrderDetailWithItem> details;

    public static class OrderDetailWithItem {
        @Embedded
        public OrderDetail orderDetail;

        @Relation(
            parentColumn = "menuItemId",
            entityColumn = "menuItemId"
        )
        public MenuItem menuItem;
    }
}
