import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

class Book implements Comparable<Book>, Serializable {
    int bookId;
    String title;
    String author;
    String category;
    boolean isIssued;

    Book(int id, String t, String a, String c) {
        bookId = id; title = t; author = a; category = c; isIssued = false;
    }

    void markAsIssued(){ isIssued = true; }
    void markAsReturned(){ isIssued = false; }

    public String toCSV(){
        return bookId + "|" + escape(title) + "|" + escape(author) + "|" + escape(category) + "|" + isIssued;
    }

    static Book fromCSV(String line){
        String[] p = splitEscaped(line);
        Book b = new Book(Integer.parseInt(p[0]), unescape(p[1]), unescape(p[2]), unescape(p[3]));
        b.isIssued = Boolean.parseBoolean(p[4]);
        return b;
    }

    public String toString(){
        return "["+bookId+"] "+title+" by "+author+" ("+category+") Issued:"+isIssued;
    }

    public int compareTo(Book o){
        return this.title.compareToIgnoreCase(o.title);
    }

    static String escape(String s){ return s.replace("|","&#124;"); }
    static String unescape(String s){ return s.replace("&#124;","|"); }
    static String[] splitEscaped(String s){
        // split on '|' not inside escapes (our simple escape replacement)
        return s.split("\\|");
    }
}

class Member implements Serializable {
    int memberId;
    String name;
    String email;
    List<Integer> issuedBooks = new ArrayList<>();
    Queue<Integer> waitQueue = new LinkedList<>();

    Member(int id, String n, String e){
        memberId = id; name = n; email = e;
    }

    void addIssuedBook(int bookId){ issuedBooks.add(bookId); }
    void returnIssuedBook(int bookId){ issuedBooks.remove(Integer.valueOf(bookId)); }
    public String toCSV(){
        return memberId + "|" + escape(name) + "|" + escape(email) + "|" + issuedBooksToString();
    }
    static Member fromCSV(String line){
        String[] p = line.split("\\|",4);
        Member m = new Member(Integer.parseInt(p[0]), unescape(p[1]), unescape(p[2]));
        if(p.length>3 && p[3].length()>0){
            for(String s: p[3].split(",")) if(!s.isEmpty()) m.issuedBooks.add(Integer.parseInt(s));
        }
        return m;
    }
    String issuedBooksToString(){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<issuedBooks.size();i++){
            if(i>0) sb.append(",");
            sb.append(issuedBooks.get(i));
        }
        return sb.toString();
    }
    static String escape(String s){ return s.replace("|","&#124;"); }
    static String unescape(String s){ return s.replace("&#124;","|"); }

    public String toString(){
        return "["+memberId+"] "+name+" <"+email+"> Issued:"+issuedBooks;
    }
}

public class LibraryManager {
    private Map<Integer, Book> books = new HashMap<>();
    private Map<Integer, Member> members = new HashMap<>();
    private Set<String> categories = new HashSet<>();
    private Map<Integer, Queue<Integer>> waitingList = new HashMap<>(); // bookId -> queue of memberIds
    private int nextBookId = 100;
    private int nextMemberId = 200;
    private final String booksFile = "books.txt";
    private final String membersFile = "members.txt";
    private Scanner sc = new Scanner(System.in);
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+\\-]+@[\\w\\-]+\\.[A-Za-z]{2,}$");

    public static void main(String[] args){
        LibraryManager lm = new LibraryManager();
        lm.loadFromFile();
        lm.mainMenu();
    }

    void mainMenu(){
        try{
            while(true){
                System.out.println("\n=== City Library Digital Management System ===");
                System.out.println("1. Add Book");
                System.out.println("2. Add Member");
                System.out.println("3. Issue Book");
                System.out.println("4. Return Book");
                System.out.println("5. Search Books");
                System.out.println("6. Sort Books");
                System.out.println("7. Show All Books / Members");
                System.out.println("8. Exit");
                System.out.print("Enter choice: ");
                String line = sc.nextLine().trim();
                if(line.isEmpty()) continue;
                int ch;
                try{ ch = Integer.parseInt(line); } catch(Exception e){ System.out.println("Invalid choice."); continue; }
                switch(ch){
                    case 1: addBook(); break;
                    case 2: addMember(); break;
                    case 3: issueBook(); break;
                    case 4: returnBook(); break;
                    case 5: searchBooks(); break;
                    case 6: sortBooksMenu(); break;
                    case 7: showAll(); break;
                    case 8: saveToFile(); System.out.println("Saved. Exiting."); return;
                    default: System.out.println("Invalid choice."); break;
                }
            }
        } finally {
            sc.close();
        }
    }

    void addBook(){
        try{
            System.out.print("Enter Book Title: ");
            String title = sc.nextLine().trim();
            System.out.print("Enter Author: ");
            String author = sc.nextLine().trim();
            System.out.print("Enter Category: ");
            String category = sc.nextLine().trim();
            if(title.isEmpty()||author.isEmpty()||category.isEmpty()){ System.out.println("Fields cannot be empty."); return; }
            Book b = new Book(nextBookId++, title, author, category);
            books.put(b.bookId, b);
            categories.add(category);
            saveToFile();
            System.out.println("Book added with ID: " + b.bookId);
        } catch(Exception e){
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    void addMember(){
        try{
            System.out.print("Enter Member Name: ");
            String name = sc.nextLine().trim();
            System.out.print("Enter Email: ");
            String email = sc.nextLine().trim();
            if(name.isEmpty()||email.isEmpty()){ System.out.println("Fields cannot be empty."); return; }
            if(!EMAIL.matcher(email).matches()){ System.out.println("Invalid email format."); return; }
            Member m = new Member(nextMemberId++, name, email);
            members.put(m.memberId, m);
            saveToFile();
            System.out.println("Member added with ID: " + m.memberId);
        } catch(Exception e){
            System.out.println("Error adding member: " + e.getMessage());
        }
    }

    void issueBook(){
        try{
            System.out.print("Enter Book ID: "); int bookId = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Enter Member ID: "); int memId = Integer.parseInt(sc.nextLine().trim());
            Book b = books.get(bookId);
            Member m = members.get(memId);
            if(b==null){ System.out.println("Book not found."); return; }
            if(m==null){ System.out.println("Member not found."); return; }
            if(b.isIssued){
                System.out.println("Book is already issued. Adding to waiting list.");
                waitingList.putIfAbsent(bookId, new LinkedList<>());
                Queue<Integer> q = waitingList.get(bookId);
                if(!q.contains(memId)){ q.add(memId); System.out.println("Added to wait list position: " + q.size()); }
                else System.out.println("Already in wait list.");
                return;
            }
            b.markAsIssued();
            m.addIssuedBook(bookId);
            saveToFile();
            System.out.println("Book issued to member.");
        } catch(NumberFormatException e){ System.out.println("Invalid ID."); }
    }

    void returnBook(){
        try{
            System.out.print("Enter Book ID: "); int bookId = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Enter Member ID: "); int memId = Integer.parseInt(sc.nextLine().trim());
            Book b = books.get(bookId);
            Member m = members.get(memId);
            if(b==null || m==null){ System.out.println("Book or member not found."); return; }
            if(!m.issuedBooks.contains(bookId)){ System.out.println("This member did not issue this book."); return; }
            b.markAsReturned();
            m.returnIssuedBook(bookId);
            // if waiting list present, issue automatically to next member
            Queue<Integer> q = waitingList.get(bookId);
            if(q!=null && !q.isEmpty()){
                int nextMem = q.poll();
                Member nm = members.get(nextMem);
                if(nm!=null){
                    b.markAsIssued();
                    nm.addIssuedBook(bookId);
                    System.out.println("Book auto-issued to waiting member ID: "+nextMem);
                }
            }
            saveToFile();
            System.out.println("Book returned successfully.");
        } catch(NumberFormatException e){ System.out.println("Invalid ID."); }
    }

    void searchBooks(){
        System.out.println("Search by: 1.Title 2.Author 3.Category");
        String s = sc.nextLine().trim();
        int ch = 0;
        try{ ch = Integer.parseInt(s);}catch(Exception e){ System.out.println("Invalid."); return; }
        System.out.print("Enter search term: ");
        String term = sc.nextLine().trim().toLowerCase();
        List<Book> res = new ArrayList<>();
        for(Book b : books.values()){
            if(ch==1 && b.title.toLowerCase().contains(term)) res.add(b);
            if(ch==2 && b.author.toLowerCase().contains(term)) res.add(b);
            if(ch==3 && b.category.toLowerCase().contains(term)) res.add(b);
        }
        if(res.isEmpty()) System.out.println("No results.");
        else res.forEach(System.out::println);
    }

    void sortBooksMenu(){
        System.out.println("Sort by: 1.Title 2.Author 3.Category");
        String s = sc.nextLine().trim();
        int ch = 0;
        try{ ch = Integer.parseInt(s);}catch(Exception e){ System.out.println("Invalid."); return; }
        List<Book> list = new ArrayList<>(books.values());
        switch(ch){
            case 1: Collections.sort(list); break;
            case 2: Collections.sort(list, Comparator.comparing(b -> b.author.toLowerCase())); break;
            case 3: Collections.sort(list, Comparator.comparing(b -> b.category.toLowerCase())); break;
            default: System.out.println("Invalid."); return;
        }
        list.forEach(System.out::println);
    }

    void showAll(){
        System.out.println("\nBooks:");
        if(books.isEmpty()) System.out.println("No books.");
        else books.values().forEach(System.out::println);
        System.out.println("\nMembers:");
        if(members.isEmpty()) System.out.println("No members.");
        else members.values().forEach(System.out::println);
        System.out.println("\nCategories: " + categories);
    }

    void saveToFile(){
        try(BufferedWriter bw = Files.newBufferedWriter(Paths.get(booksFile))){
            for(Book b : books.values()) bw.write(b.toCSV()+"\n");
        } catch(IOException e){ System.out.println("Error saving books: "+e.getMessage()); }
        try(BufferedWriter bw = Files.newBufferedWriter(Paths.get(membersFile))){
            for(Member m : members.values()) bw.write(m.toCSV()+"\n");
        } catch(IOException e){ System.out.println("Error saving members: "+e.getMessage()); }
    }

    void loadFromFile(){
        try{
            if(Files.exists(Paths.get(booksFile))){
                List<String> lines = Files.readAllLines(Paths.get(booksFile));
                for(String line: lines){
                    if(line.trim().isEmpty()) continue;
                    Book b = Book.fromCSV(line);
                    books.put(b.bookId, b);
                    nextBookId = Math.max(nextBookId, b.bookId+1);
                    categories.add(b.category);
                }
            }
        } catch(IOException e){ System.out.println("Error loading books: "+e.getMessage()); }

        try{
            if(Files.exists(Paths.get(membersFile))){
                List<String> lines = Files.readAllLines(Paths.get(membersFile));
                for(String line: lines){
                    if(line.trim().isEmpty()) continue;
                    Member m = Member.fromCSV(line);
                    members.put(m.memberId, m);
                    nextMemberId = Math.max(nextMemberId, m.memberId+1);
                }
            }
        } catch(IOException e){ System.out.println("Error loading members: "+e.getMessage()); }
    }
}
