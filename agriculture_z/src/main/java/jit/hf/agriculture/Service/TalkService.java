package jit.hf.agriculture.Service;


import jit.hf.agriculture.domain.Talk;

public interface TalkService {
    //新增、编辑、保存话题
    Talk saveOrUpdateQuestion(Talk talk);

}
