package tools;

public class LinkedList<T>
{
  protected class Node<U>
  {
    public U value;
    public Node<U> next;
    
    public Node()
    {
      value = null;
      next = null;
    }
    
    public Node(U value)
    {
      this.value = value;
      this.next = null;
    }
    
    public Node(U value, Node<U> next)
    {
      this.value = value;
      this.next = next;
    }
  }
  
  protected Node<T> head;
  protected int nElements;
  
  public LinkedList()
  {
    head = null;
    nElements = 0;
  }
  
  public void add(T value)
  {
    if(head == null)
    {
      head = new Node<T>(value);
      nElements++;
    }
    else
    {
      Node<T> p = head;
      
      while(p.next != null)
        p = p.next;
      
      p.next = new Node<T>(value);
      nElements++;
    }
  }
  
  public T get(int index)
  {
	  /*
    if(index >= nElements)
      throw new IndexOutOfBoundsException("index >= elements");
      */
	  
	  if(index >= nElements || index < 0)
		  return head.value;
    
    Node<T> p = head;
    
    for(int i = 0; i < index; ++i)
      p = p.next;
    
    return p.value;
  }
  
  public T remove(int index)
  {
    if(index >= nElements)
      throw new IndexOutOfBoundsException("index >= elements");
    
    if(head == null)
      return null;
    
    T tmp = null;
    Node<T> p = head;
    Node<T> pOld = p;
    
    if(index == 0)
    {
      tmp = p.value;
      pOld = p.next;
      head = pOld;
      nElements--;
      
      return tmp;
    }
    
    for(int i = 0; i < index; ++i)
    {
      pOld = p;
      p = p.next;
    }
    
    tmp = p.value;
    pOld.next = p.next;
    nElements--;
    
    return tmp;
  }
  
  public void clear()
  { 
    head = null;
    nElements = 0;
  }
  
  public boolean isEmpty()
  {
    return head == null;
  }
  
  public int size()
  {
    return nElements;
  }
}
