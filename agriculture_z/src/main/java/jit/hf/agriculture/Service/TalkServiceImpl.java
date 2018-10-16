package jit.hf.agriculture.Service;

import jit.hf.agriculture.Repository.TalkRepository;
import jit.hf.agriculture.domain.Talk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TalkServiceImpl implements TalkService {

    @Autowired
    TalkRepository talkRepository;

    @Override
    public Talk saveOrUpdateQuestion(Talk talk) {
        return talkRepository.save( talk );
    }
}
