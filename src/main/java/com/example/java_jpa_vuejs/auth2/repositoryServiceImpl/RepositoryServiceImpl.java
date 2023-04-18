package com.example.java_jpa_vuejs.auth2.repositoryServiceImpl;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.java_jpa_vuejs.auth2.entity.Member;
import com.example.java_jpa_vuejs.auth2.entity.Phone;
import com.example.java_jpa_vuejs.auth2.repositoryJPA.MemRepository;
import com.example.java_jpa_vuejs.auth2.repositoryJPA.PhoneRepository;
import com.example.java_jpa_vuejs.auth2.repositoryService.RepositoryService;

@Service
public class RepositoryServiceImpl implements RepositoryService {
	
	@Autowired
	private MemRepository membRepository;

	@Autowired
	private PhoneRepository phoneRepository;

	
    public void saveMember(){
		Member first  = new Member("Jung");
		Member second = new Member("Dong");
		Member third  = new Member("Min");
		
		Phone p = new Phone(first, "010-XXXX-YYYY");
		
		membRepository.save(first);
		membRepository.save(second);
		membRepository.save(third);
		
		phoneRepository.save(p);
	}

	public void print(){
		// @ManyToOne의 fetch 기본전략은 EAGER이다.
		// 따라서 @Transactional 어노테이션이 없더라도
		// 기본적으로 전부 데이터를 적재한다.
		List<Phone> phone = phoneRepository.findAll();
		for( Phone p : phone ){
			System.out.println(p.toString()+ " " + p.getMember().toString());
		}
	}

	@Transactional
	public void lazyPrint(){
		// @OneToMany의 fetch 기본전략은 LAZY이다.		
		// 따라서 Member Entity 내부의 Phone 콜렉션은 
		// LAZY 전략이기 때문에 @Transactional 어노테이션이 있어야 한다.
		List<Member> member = membRepository.findAll();
		for( Member m : member ) {
			System.out.println(m.toString());
			for( Phone e : m.getPhone() ){
				System.out.println(e.toString());
			}
		}
	}

	public void lazyPrint2(){
		// Entity가 LAZY 전략일지라도 
		// LAZY 전략을 쓰는 객체를 사용하지 않는다면
		// @Transactional 어노테이션이 없어도 된다.
        
		List<Member> member = membRepository.findAll();
		for( Member m : member ) {
			System.out.println(m.toString());
		}
	}

	public void deletAll() {
		membRepository.deleteAll();
		phoneRepository.deleteAll();
	}	
    
}