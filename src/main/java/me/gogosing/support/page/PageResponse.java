package me.gogosing.support.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PageResponse<T> {

  private int pageSize;

  private int pageNumber;

  private int totalPageNumber;

  private Long totalSize;

  private List<T> list;

  public PageResponse(int pageSize, int pageNumber, int totalPageNumber, Long totalSize,
      List<T> list) {
    this.pageSize = pageSize;
    this.pageNumber = pageNumber;
    this.totalPageNumber = totalPageNumber;
    this.totalSize = totalSize;
    this.list = list;
  }

  public static <T> PageResponse<T> empty(PageOptions pageOptions) {
    return new PageResponse<>(pageOptions.getPageSize(), pageOptions.getPageNumber() + 1, 0, 0L,
        Collections.emptyList());
  }

  public static <T> PageResponse<T> empty(Pageable pageable) {
    PageResponse<T> response = new PageResponse<>();
    response.setPageNumber(pageable.getPageNumber() + 1);
    response.setPageSize(pageable.getPageSize());
    response.setTotalPageNumber(0);
    response.setTotalSize(0L);
    response.setList(new ArrayList<>());
    return response;
  }

  public static <T> PageResponse<T> convert(Page<T> page) {
    PageResponse<T> response = new PageResponse<>();
    response.setPageNumber(page.getNumber() + 1);
    response.setPageSize(page.getSize());
    response.setTotalPageNumber(page.getTotalPages());
    response.setTotalSize(page.getTotalElements());
    response.setList(page.getContent());
    return response;
  }

  public static <T> PageResponse<T> mergeLists(
      @Nonnull List<T> one, @Nonnull List<T> two, @Nonnull Comparator<T> comparator,
      int pageNumber, int pageSize) {
    final long totalSize = one.size() + two.size();
    final int totalPageNumber = (int) Math.ceil((double) totalSize / (double) pageSize);
    return new PageResponse<>(
        pageSize, pageNumber, totalPageNumber, totalSize,
        Stream.concat(one.stream(), two.stream())
            .sorted(comparator)
            .skip((long) pageNumber * pageSize)
            .limit(pageSize)
            .collect(Collectors.toList())
    );
  }

}
