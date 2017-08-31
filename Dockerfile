FROM gbaydin/pyprob

RUN add-apt-repository ppa:openjdk-r/ppa
RUN apt update
RUN apt install -y openjdk-8-jdk

RUN apt install -y wget vim

RUN mkdir /code/lein
RUN wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -P /code/lein/
RUN chmod a+x /code/lein/lein

ENV PATH="/code/lein:${PATH}"
ENV LEIN_ROOT="true"

RUN lein

RUN cd /code && git clone -b develop https://github.com/JonyEpsilon/gorilla-repl.git
RUN cd /code/gorilla-repl && lein install

RUN echo "{:user {:plugins [[lein-gorilla \"0.4.0\"]]}}" > ~/.lein/profiles.clj
RUN lein

RUN cd /code && git clone -b development https://bitbucket.org/probprog/anglican.git
RUN cd /code/anglican && lein install

RUN cd /tmp && wget https://gist.githubusercontent.com/gbaydin/bdcd16e404a57c6f1e9e25c7b2884438/raw/543d7bc8067430211008fb0a03f8e2a5c47a28ea/ppaml-ss-examples-project.clj -O project.clj && lein deps

ARG GIT_COMMIT="unknown"
LABEL git_commit=$GIT_COMMIT

RUN mkdir /code/anglican-infcomp
COPY . /code/anglican-infcomp
RUN cd /code/anglican-infcomp && lein install

WORKDIR /workspace
RUN chmod -R a+w /workspace
CMD bash
