package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, Book param) {
        Item findItem = itemRepository.findOne(itemId);
        changeBooksAllInfo(param, findItem);
    }

    private static void changeBooksAllInfo(Book param, Item findItem) {
        if (findItem instanceof Book) {
            Book book = (Book)findItem;
            book.setPrice(param.getPrice());
            book.setName(param.getName());
            book.setStockQuantity(param.getStockQuantity());
            book.setAuthor(param.getAuthor());
            book.setIsbn(param.getIsbn());
        }
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
