package maas.models;
import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class Order implements Serializable, Comparable<Order> {
	
	private String guid;
	private String customer_id;
	private Date order_date;
	private Date delivery_date;
	private List<ProductsToOrder> products;
	
	public Order(String guid, String customerId, Date orderDate, Date deliveryDate, List<ProductsToOrder> products) {
		this.guid = guid;
		this.customer_id = customerId;
		this.order_date = orderDate;
		this.delivery_date = deliveryDate;
		this.products = products;
	}
	
	public String getGuid() {
		return guid;
	}
	
	public String getCustomerId() {
		return customer_id;
	}
	
	public Date getOrderDate() {
		return order_date;
	}
	
	public Date getDeliveryDate() {
		return delivery_date;
	}
	
	public List<ProductsToOrder> getProducts() {
		return products;
	}
	
	public String[] getProductIds() {
		String[] productIds = new String[products.size()];
		
		for(int i = 0; i < products.size(); ++i) {
			productIds[i] = products.get(i).getProductid();
		}
		
		return productIds;
	}

	@Override
	public int compareTo(Order o) {
		return this.delivery_date.compareTo(o.delivery_date);
	}

	@Override
	public String toString() {
		return "Order [guid=" + guid + ", customer_id=" + customer_id + ", order_date=" + order_date
				+ ", delivery_date=" + delivery_date + ", products=" + products + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((customer_id == null) ? 0 : customer_id.hashCode());
		result = prime * result + ((delivery_date == null) ? 0 : delivery_date.hashCode());
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result + ((order_date == null) ? 0 : order_date.hashCode());
		result = prime * result + ((products == null) ? 0 : products.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Order)) {
			return false;
		}
		return (this.guid.equals((((Order) obj).getGuid())));
	}
	
}
