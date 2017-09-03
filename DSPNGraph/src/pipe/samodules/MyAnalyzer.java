package pipe.samodules;



import it.unifi.oris.sirio.analyzer.AnalyzerComponentsFactory;
import it.unifi.oris.sirio.analyzer.AnalyzerObserver;
import it.unifi.oris.sirio.analyzer.EnabledEventsBuilder;
import it.unifi.oris.sirio.analyzer.Event;
import it.unifi.oris.sirio.analyzer.Succession;
import it.unifi.oris.sirio.analyzer.SuccessionEvaluator;
import it.unifi.oris.sirio.analyzer.SuccessionProcessor;
import it.unifi.oris.sirio.analyzer.enumeration_policy.EnumerationPolicy;
import it.unifi.oris.sirio.analyzer.graph.SuccessionGraph;
import it.unifi.oris.sirio.analyzer.state.State;
import it.unifi.oris.sirio.analyzer.stop_criterion.StopCriterion;
import it.unifi.oris.sirio.math.OmegaBigDecimal;
import it.unifi.oris.sirio.models.stpn.StochasticStateFeature;
import it.unifi.oris.sirio.models.stpn.StochasticTransitionFeature;
import it.unifi.oris.sirio.petrinet.Transition;
import it.unifi.oris.sirio.petrinet.TransitionFeature;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JTextField;

public class MyAnalyzer<M, E extends Event> {

	private int i = 0;
	private Set<AnalyzerObserver> observers = new LinkedHashSet();
	private SuccessionGraph graph = new SuccessionGraph();

	private M model;
	private State initialState;
	private AnalyzerComponentsFactory<M, E> componentsFactory;
	EnumerationPolicy enumerationPolicy;
	EnabledEventsBuilder<M, E> enabledEventsBuilder;
	SuccessionEvaluator<M, E> successionEvaluator;
	// 加一个
	MyStochasticSuccessionEvaluator<M, E> mysuccessionEvaluator;
	SuccessionProcessor preProcessor;
	SuccessionProcessor postProcessor;
	StopCriterion globalStopCriterion;
	StopCriterion localStopCriterion;

	SuccessionEvaluator<M, E> successionEvaluator1;

	public MyAnalyzer(AnalyzerComponentsFactory<M, E> componentsFactory,
			M model, State initialState) {
		this.componentsFactory = componentsFactory;
		this.model = model;
		this.initialState = initialState;
	}

	public void addObserver(AnalyzerObserver observer) {
		this.observers.add(observer);
	}

	public void removeObserver(AnalyzerObserver observer) {
		this.observers.remove(observer);
	}

	public SuccessionGraph analyze(JTextField test) {
		this.enumerationPolicy = this.componentsFactory.getEnumerationPolicy();
		this.enabledEventsBuilder = this.componentsFactory
				.getEnabledEventsBuilder();
		this.successionEvaluator = this.componentsFactory
				.getSuccessionEvaluator();
		this.preProcessor = this.componentsFactory.getPreProcessor();
		this.postProcessor = this.componentsFactory.getPostProcessor();
		this.globalStopCriterion = this.componentsFactory
				.getGlobalStopCriterion();
		this.localStopCriterion = this.componentsFactory
				.getLocalStopCriterion();

		this.mysuccessionEvaluator = new MyStochasticSuccessionEvaluator(true,
				new it.unifi.oris.sirio.models.pn.PetriTokensRemover(),
				new it.unifi.oris.sirio.models.pn.PetriTokensAdder(), false,
				new OmegaBigDecimal("5"));
		// 我觉得这个不重要，试试先写个数字

		this.successionEvaluator1 = (SuccessionEvaluator<M, E>) this.mysuccessionEvaluator;
		Succession initialSuccession = new Succession(null, null,
				this.initialState);
		notifySuccessionCreated(initialSuccession);

		initialSuccession = this.postProcessor.process(initialSuccession);
		notifySuccessionPostProcessed(initialSuccession);

		if (initialSuccession != null) {
			this.enumerationPolicy.add(initialSuccession);
			notifySuccessionInserted(initialSuccession);
		}
		i = 1;

		do {
			test.setText("enumerating "+ String.valueOf(i)+" classes");
			// FileUtils.writefile(String.valueOf(i));

			Succession currentSuccession = this.enumerationPolicy.remove();
			notifySuccessionExtracted(currentSuccession);

			currentSuccession = this.preProcessor.process(currentSuccession);
			notifySuccessionPreProcessed(currentSuccession);

			boolean newChild = this.graph.addSuccession(currentSuccession);
			// System.out.println("newchild="+newChild);
			notifyNodeAdded(currentSuccession);

			if ((newChild) && (!this.localStopCriterion.stop())) {
				i++;
				for (E e : this.enabledEventsBuilder.getEnabledEvents(
						this.model, currentSuccession.getChild())) {
					Succession childSuccession = this.successionEvaluator
							.computeSuccession(this.model,
									currentSuccession.getChild(), e);

					if (childSuccession != null) {
						notifySuccessionCreated(childSuccession);

						childSuccession = this.postProcessor
								.process(childSuccession);
						notifySuccessionPostProcessed(childSuccession);

						this.enumerationPolicy.add(childSuccession);
						notifySuccessionInserted(childSuccession);
					}

					if (this.globalStopCriterion.stop()) {
						break;
					}
				}
			} else if ((newChild) && (this.localStopCriterion.stop())) { 
				// 完成带停止条件的瞬态分析的关键就在于，当满足停止条件时，用MyStochasticSuccessionEvaluator
				// 这里面更改了MyPetriSuccessionEvaluator,从而使得最后概率和为1，而不是到了停止的状态
				// 就漏掉很多概率
				// System.out.println("满足localStopCriterion");
				if (!(currentSuccession.getChild().getFeature(
						StochasticStateFeature.class).isAbsorbing())) {
					for (E e : this.enabledEventsBuilder.getEnabledEvents(
							this.model, currentSuccession.getChild())) {
						Succession childSuccession = this.successionEvaluator1
								.computeSuccession(this.model,
										currentSuccession.getChild(), e);
						// 这里要用MyStochasticSuccessionEvaluator
						if (childSuccession != null) {
							notifySuccessionCreated(childSuccession);

							// childSuccession =
							// this.postProcessor.process(childSuccession);
							notifySuccessionPostProcessed(childSuccession);

							this.enumerationPolicy.add(childSuccession);
							notifySuccessionInserted(childSuccession);
						}

						if (this.globalStopCriterion.stop()) {

							break;
						}
					}
				}
			}
			if (this.enumerationPolicy.isEmpty())
				break;
		} while (!this.globalStopCriterion.stop());

		while (!this.enumerationPolicy.isEmpty()) {
			Succession finalSuccession = this.enumerationPolicy.remove();
			notifySuccessionExtracted(finalSuccession);

			finalSuccession = this.preProcessor.process(finalSuccession);
			notifySuccessionPreProcessed(finalSuccession);

			this.graph.addSuccession(finalSuccession);
			notifyNodeAdded(finalSuccession);
		}

		return this.graph;
	}

	private void notifySuccessionCreated(Succession succession) {
		this.globalStopCriterion.notifySuccessionCreated(succession);
		this.localStopCriterion.notifySuccessionCreated(succession);
		for (AnalyzerObserver o : this.observers)
			o.notifySuccessionCreated(succession);
	}

	private void notifySuccessionPostProcessed(Succession succession) {
		this.globalStopCriterion.notifySuccessionPostProcessed(succession);
		this.localStopCriterion.notifySuccessionPostProcessed(succession);
		for (AnalyzerObserver o : this.observers)
			o.notifySuccessionPostProcessed(succession);
	}

	private void notifySuccessionInserted(Succession succession) {
		this.globalStopCriterion.notifySuccessionInserted(succession);
		this.localStopCriterion.notifySuccessionInserted(succession);
		for (AnalyzerObserver o : this.observers)
			o.notifySuccessionInserted(succession);
	}

	private void notifySuccessionExtracted(Succession succession) {
		this.globalStopCriterion.notifySuccessionExtracted(succession);
		this.localStopCriterion.notifySuccessionExtracted(succession);
		for (AnalyzerObserver o : this.observers)
			o.notifySuccessionExtracted(succession);
	}

	private void notifySuccessionPreProcessed(Succession succession) {
		this.globalStopCriterion.notifySuccessionPreProcessed(succession);
		this.localStopCriterion.notifySuccessionPreProcessed(succession);
		for (AnalyzerObserver o : this.observers)
			o.notifySuccessionPreProcessed(succession);
	}

	private void notifyNodeAdded(Succession succession) {
		this.globalStopCriterion.notifyNodeAdded(succession);
		this.localStopCriterion.notifyNodeAdded(succession);
		for (AnalyzerObserver o : this.observers) {
			o.notifyNodeAdded(succession);
		}
	}
}


