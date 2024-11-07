package appication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

import db.DB;
import model.exceptions.DbException;

public class App {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in).useLocale(Locale.US);
	
		Connection conn = null;
		PreparedStatement ps = null;
		Statement st = null;
		ResultSet rs = null;
		int j = 1;
		
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		try {
			
			conn = DB.getConnection();
			
			System.out.println("Quantos clientes: ");
			int x = sc.nextInt();
			sc.nextLine();
			for(int i = 0; i < x; i++) {
				System.out.print("Digite o nome do cliente: ");
				String nameCliente = sc.nextLine();
				
				System.out.print("Digite o email do cliente: ");
				String emailCliente = sc.nextLine();
				
				ps = conn.prepareStatement(
						"INSERT INTO clientes"
						+ "(nome, email) "
						+ "VALUES "
						+ "(?, ?) ",
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, nameCliente);
				ps.setString(2, emailCliente);
				
				int rows = ps.executeUpdate();
				if(rows > 0) {
					ResultSet result = ps.getGeneratedKeys();
					while(result.next()) {
						int id = result.getInt(1);
						System.out.println("Id do novo cliente: " + id);
					}
				}
					
			}
			
			System.out.print("Quantos produtos: ");
			int l = sc.nextInt();
			sc.nextLine();
			for(int i = 0; i < l; i++) {
				System.out.print("Nome do produtos: ");
				String nameProduct= sc.nextLine();
				System.out.print("Preço do produto: ");
				Double priceProduct = sc.nextDouble();
				sc.nextLine();
				
				ps = conn.prepareStatement(
						"INSERT INTO produtos"
						+ "(nome, preco) "
						+ "VALUES "
						+ "(?,?)",
						Statement.RETURN_GENERATED_KEYS);
				
				ps.setString(1, nameProduct);
				ps.setDouble(2, priceProduct);
				int rows = ps.executeUpdate();
				if(rows > 0) {
					rs = ps.getGeneratedKeys();
					while(rs.next()) {
						int id = rs.getInt(1);
						System.out.println("Id do novo produto: " + id);
					}
				}
			}
			
			System.out.println("Produtos disponíveis: ");
			st = conn.createStatement();
			rs = st.executeQuery("select * from produtos");
		
			while(rs.next()) {
				System.out.println(rs.getInt("id") + ", " + rs.getString("nome"));
			}
			
			System.out.println("Clientes registrados: ");
			rs = st.executeQuery("select * from clientes");
			while(rs.next()) {
				System.out.println(rs.getInt("id") + ", " + rs.getString("nome"));
			}
			
			
			
			
			System.out.println("Cadastrar pedidos: ");
			System.out.println("Digite -1 para sair: ");
			int id = 0;
			while(id != -1) {
				System.out.print("Digite o id do cliente: ");
				id = sc.nextInt();
				if(id == -1) {
					break;
				}
				sc.nextLine();
				System.out.print("Digite a data do pedido: ");
				String stringDate = sc.nextLine();
				LocalDate date = LocalDate.parse(stringDate, fmt);
				
				ps = conn.prepareStatement(
						"INSERT INTO pedidos "
						+ "(cliente_id, data_pedido) "
						+ "VALUES "
						+ "(?, ?)",
						Statement.RETURN_GENERATED_KEYS);
			
				ps.setInt(1, id);
				ps.setDate(2, java.sql.Date.valueOf(date));
				
				int rows = ps.executeUpdate();
				ResultSet generatedId = ps.getGeneratedKeys();
				if(rows > 0) {
					while(generatedId.next()) {
						id = generatedId.getInt(1);
						System.out.println("Pedido de id #" + id + " adicionado");
					}
				}
			}
			
			
			System.out.println("Adicionando pedido:");
			System.out.println("Digite -1 para sair: ");
			int opc = 0;
			while(opc != -1) {
				System.out.print("Digite o numero do pedido: ");
				opc = sc.nextInt();
				if(opc == -1) {
					break;
				}
				sc.nextLine();
				System.out.print("Digite a quantidade do pedido: ");
				int quantity = sc.nextInt();
				
				ps = conn.prepareStatement(
						"INSERT INTO pedido_produtos "
						+ "(pedido_id, produto_id, quantidade) "
						+ "VALUES "
						+ "(?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS);
				
				ps.setInt(1, j);
				ps.setInt(2, opc);
				ps.setInt(3, quantity);
				
				j++;
				
				int rows = ps.executeUpdate();
				ResultSet generatedPedido = ps.getGeneratedKeys();
				if(rows > 0) {
					while(generatedPedido.next()) {
						id = generatedPedido.getInt(1);
						System.out.println("Pedido de #" + id + " feito com sucesso");
					}
				}
				
			}
			
			
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeConnection();
			DB.closeStatement(st);
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
		
	}
	
}
