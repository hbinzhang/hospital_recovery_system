package com.xmc.hospitalrec.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xmc.hospitalrec.model.PageQueryCondition;

@Transactional(propagation = Propagation.SUPPORTS)
public abstract class AbstractDao {

	@PersistenceContext
	protected EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@SuppressWarnings("rawtypes")
	public void insert(Object o) {
		if (o == null)
			return;
		if (o instanceof List) {
			for (Object e : (List) o) {
				insert(e);
			}
		} else
			entityManager.persist(o);
	}

	@SuppressWarnings("rawtypes")
	public void delete(Object o) {
		if (o == null)
			return;
		if (o instanceof List) {
			for (Object e : (List) o) {
				delete(e);
			}
		} else
			entityManager.remove(o);
	}

	@SuppressWarnings("rawtypes")
	public void update(Object o) {
		if (o == null)
			return;
		if (o instanceof List) {
			for (Object e : (List) o) {
				update(e);
			}
		} else
			entityManager.merge(o);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int update(Class clazz, Map<String, Object> conditionMap, Map<String, Object> valueMap) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaUpdate criteriaUpdate = criteriaBuilder.createCriteriaUpdate(clazz);
		Root root = criteriaUpdate.from(clazz);
		for (String key : valueMap.keySet()) {
			criteriaUpdate.set(key, valueMap.get(key));
		}

		Predicate condition = generatePredicate(criteriaBuilder, root, conditionMap);
		if (condition != null)
			criteriaUpdate.where(condition);

		Query query = entityManager.createQuery(criteriaUpdate);
		int rowCount = query.executeUpdate();

		return rowCount;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean exists(Class clazz, Map<String, Object> conditionMap) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(clazz);
		Root root = criteriaQuery.from(clazz);
		criteriaQuery.select(root);

		Predicate condition = generatePredicate(criteriaBuilder, root, conditionMap);
		if (condition != null)
			criteriaQuery.where(condition);

		criteriaQuery.select(criteriaBuilder.count(root));
		return ((long) this.entityManager.createQuery(criteriaQuery).getResultList().get(0)>0);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int delete(Class clazz, Map<String, Object> conditionMap) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaDelete CriteriaDelete = criteriaBuilder.createCriteriaDelete(clazz);
		Root root = CriteriaDelete.from(clazz);

		Predicate condition = generatePredicate(criteriaBuilder, root, conditionMap);
		if (condition != null)
			CriteriaDelete.where(condition);

		Query query = entityManager.createQuery(CriteriaDelete);
		int rowCount = query.executeUpdate();

		return rowCount;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Predicate generatePredicate(CriteriaBuilder criteriaBuilder, Root root,
			Map<String, Object> conditionMap) {
		Predicate condition = null;
		if (conditionMap == null)
			return null;
		for (String key : conditionMap.keySet()) {
			Predicate tmpCondition = null;
			if (key.endsWith("End")) {
				String realKey = key.substring(0, key.length() - 3);
				tmpCondition = criteriaBuilder.lessThan(getPath(root, realKey), (Comparable) conditionMap.get(key));
			} else if (key.endsWith("Begin")) {
				String realKey = key.substring(0, key.length() - 5);
				tmpCondition = criteriaBuilder.greaterThanOrEqualTo(getPath(root, realKey), (Comparable) conditionMap.get(key));
			} else {
				Object value = conditionMap.get(key);
				if(value instanceof Collection) {
					if(!((Collection) value).isEmpty())
						tmpCondition = getPath(root, key).in(value);
				} else if(value instanceof String) {
					String strValue = (String)value;
					if(strValue.indexOf("%")>=0) {
						tmpCondition = criteriaBuilder.like(getStringPath(root, key), strValue);
					} else
						tmpCondition = criteriaBuilder.equal(getPath(root, key), strValue);
				} else 
					tmpCondition = criteriaBuilder.equal(getPath(root, key), value);
			}
			if (condition == null)
				condition = tmpCondition;
			else
				condition = criteriaBuilder.and(condition, tmpCondition);
		}
		return condition;
	}
	
	//@SuppressWarnings("rawtypes")
	protected Path<String> getStringPath(Root<String> root, String key) {
		if(key.indexOf(".")==-1)
			return root.get(key);
		else {
			String[] subKeys = key.split("\\.");
			Path<String> p = null;
			for(String subKey:subKeys) {
				p = (p == null) ? root.get(subKey) : p.get(subKey);
			}
			return p;
		}
	}

	@SuppressWarnings("rawtypes")
	protected Path getPath(Root root, String key) {
		if(key.indexOf(".")==-1)
			return root.get(key);
		else {
			String[] subKeys = key.split("\\.");
			Path p = null;
			for(String subKey:subKeys) {
				p = (p == null) ? root.get(subKey) : p.get(subKey);
			}
			return p;
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public List get(Class clazz) {
		return get(clazz, new PageQueryCondition());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List get(Class clazz, PageQueryCondition pageQueryCondition) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(clazz);
		Root root = criteriaQuery.from(clazz);
		criteriaQuery.select(root);

		Map<String, Object> conditionMap = pageQueryCondition.getQueryCondition();
		Predicate condition = generatePredicate(criteriaBuilder, root, conditionMap);
		if (condition != null)
			criteriaQuery.where(condition);

		Selection selection = criteriaQuery.getSelection();
		criteriaQuery.select(criteriaBuilder.count(root));
		pageQueryCondition.setTotal((long) this.entityManager.createQuery(criteriaQuery).getResultList().get(0));

		criteriaQuery.select(selection);

		setOrder(pageQueryCondition, criteriaBuilder, criteriaQuery, root);
		TypedQuery typedQuery = this.entityManager.createQuery(criteriaQuery);
		setPaging(pageQueryCondition, typedQuery);

		List result = typedQuery.getResultList();
		return result;
	}

	@SuppressWarnings("rawtypes")
	public List get(Class clazz, String primaryKeyName, Object primaryKeyValue) {
		PageQueryCondition pageQueryCondition = new PageQueryCondition("", "", "");
		pageQueryCondition.addQueryConditon(primaryKeyName, primaryKeyValue);
		return get(clazz, pageQueryCondition);
	}

	@SuppressWarnings("rawtypes")
	public Object getUnique(Class clazz, String primaryKeyName, Object primaryKeyValue) {
		List list = get(clazz, primaryKeyName, primaryKeyValue);
		if (list.size() != 1)
			return null;
		return list.get(0);
	}

	@SuppressWarnings("rawtypes")
	protected void setPaging(PageQueryCondition cond, TypedQuery typedQuery) {
		String offset = cond.getOffset();
		String limit = cond.getLimit();
		if (!(offset.equals("") && limit.equals(""))) {
			int pageSize = Integer.valueOf(limit);

			typedQuery.setFirstResult((Integer.valueOf(offset) - 1) * pageSize);
			typedQuery.setMaxResults(Integer.valueOf(limit));
		}
	}

	@SuppressWarnings("rawtypes")
	protected void setOrder(PageQueryCondition cond, CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
			Root r) {
		String sort = cond.getSort();
		String s1, s2;
		if (!sort.equals("")) {
			String[] sArray = sort.split(",");
			for (int i = 0; i < sArray.length; i++) {
				String words[] = sArray[i].trim().split(" ");
				if (words.length != 2)
					continue;

				s1 = words[1].trim();
				s2 = words[0].trim();
				if (s1.equalsIgnoreCase("asc")) {
					criteriaQuery.orderBy(criteriaBuilder.asc(r.get(s2)));
				} else if (s1.equalsIgnoreCase("desc")) {
					criteriaQuery.orderBy(criteriaBuilder.desc(r.get(s2)));
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Boolean> getConflictResults(Map<String, Object> checkProps, Class clazz) {
		Map<String, Boolean> mapResult = new HashMap<String, Boolean>();
		long count = 0;

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(clazz);
		Root root = criteriaQuery.from(clazz);
		criteriaQuery.select(root);
		criteriaQuery.select(criteriaBuilder.count(root));

		for (String key : checkProps.keySet()) {
			Predicate condition = criteriaBuilder.equal(getPath(root, key), checkProps.get(key));
			criteriaQuery.where(condition);
			count = (long) this.entityManager.createQuery(criteriaQuery).getResultList().get(0);

			mapResult.put(key, (count == 0) ? false : true);
		}
		return mapResult;
	}
	
	public Object find(Class<?> clazz, Object id) {
		return entityManager.find(clazz, id);
	}

	public Object find(Class<?> clazz, Object id, Map<String, Object> map) {
		return entityManager.find(clazz, id, map);
	}
}
