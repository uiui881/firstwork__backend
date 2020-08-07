package com.firstwork.springboot.web;

import com.firstwork.springboot.domain.posts.Posts;
import com.firstwork.springboot.domain.posts.PostsRepository;
import com.firstwork.springboot.web.dto.PostsSaveRequestDto;
import com.firstwork.springboot.web.dto.PostsUpdateRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PostsRepository postsRepository;
    
    @After
    public void tearDown() throws Exception{
        postsRepository.deleteAll();
    }
    
    @Test
    public void Posts_enroll() throws Exception {

        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder().title(title).content(content).author("author").build();
        String url = "http://localhost:" + port + "/api/v1/posts";

        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);
    }
        @Test
        public void Posts_modify() throws Exception{
            Posts savedPosts = postsRepository.save(Posts.builder().title("title").content("content").author("author").build());

            Long updateId = savedPosts.getId();
            String expectedTitle = "title2";
            String expectedContent ="content2";

            PostsUpdateRequestDto requestDto2 = PostsUpdateRequestDto.builder().title(expectedTitle).content(expectedContent).build();

            String url2 = "http://localhost:"+port+"/api/v1/posts/"+updateId;
            HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto2);

            ResponseEntity<Long> responseEntity1 = restTemplate.exchange(url2, HttpMethod.PUT, requestEntity, Long.class);
            assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity1.getBody()).isGreaterThan(0L);

            List<Posts> all2 = postsRepository.findAll();
            assertThat(all2.get(0).getTitle()).isEqualTo(expectedTitle);
            assertThat(all2.get(0).getContent()).isEqualTo(expectedContent);
        }

}
